// 统一请求封装：默认走后端服务，开发环境对部分业务可回退到本地 mock（不含 /chat）

const BASE_URL = (import.meta && import.meta.env && import.meta.env.VITE_API_BASE) || 'http://localhost:8080/api'
const USE_PARTIAL_MOCK = process.env.NODE_ENV === 'development'

// 可选：延迟模拟器（开发阶段更贴近真实网络）
const delay = (ms) => new Promise((r) => setTimeout(r, ms))

let mock = null
if (USE_PARTIAL_MOCK) {
  try {
    mock = await import('../mock/api')
  } catch (e) {
    mock = null
  }
}

const request = (method, url, data = {}, options = {}) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: url.startsWith('http') ? url : `${BASE_URL}${url}`,
      method,
      data,
      header: {\r
        'Content-Type': 'application/json',
        ...(options.headers || {}),
      },
      success: async (res) => {
        const body = res.data || {}
        // 兼容常见后端响应结构 { code, data, msg } 或直接数据
        if (typeof body === 'object' && 'code' in body) {
          if (body.code === 200) return resolve(body.data ?? body)
          return reject(new Error(body.msg || '请求失败'))
        }
        resolve(body)
      },
      fail: (err) => reject(err),
    })
  })
}

const http = {
  async get(url, params = {}, options = {}) {
    // 开发环境下：对基础资源可回退 mock，加快联调（AI接口严格走后端）
    if (USE_PARTIAL_MOCK && mock) {
      if (url === '/boxes') {
        const res = mock.getBoxes()
        await delay(200)
        return res.data || res
      }
      if (url === '/items') {
        const res = mock.getItems(params)
        await delay(200)
        return res.data || res
      }
      if (url === '/user/info') {
        const res = mock.getUserInfo()
        await delay(200)
        return res.data || res
      }
    }
    return request('GET', url, params, options)
  },

  async post(url, data = {}, options = {}) {
    // AI 接口必须走后端（根据选题报告要求）
    return request('POST', url, data, options)
  },

  setToken(token) {
    // 可扩展：把 token 存到 storage，或在 header 拦截里统一注入
    uni.setStorageSync('token', token)
  },
}

export default http
