import { useUserStore } from '@/stores/user'

const request = {
    baseURL: '/api',
    timeout: 10000,
    header: {
        'Content-Type': 'application/json'
    }
}

// 请求拦截器
request.beforeRequest = (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
        config.header['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
}

// 响应拦截器
request.afterResponse = (response) => {
    const { code, message, data } = response.data

    if (code === 200) {
        return data
    }

    if (code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        uni.redirectTo({ url: '/pages/login/login' })
    }

    uni.showToast({
        title: message || '请求失败',
        icon: 'error'
    })
    return Promise.reject(new Error(message))
}

// 统一请求方法
const http = {
    get(url, params = {}) {
        return new Promise((resolve, reject) => {
            uni.request({
                ...request,
                url: request.baseURL + url,
                data: params,
                method: 'GET',
                success: (res) => resolve(request.afterResponse(res)),
                fail: reject
            })
        })
    },

    post(url, data = {}) {
        return new Promise((resolve, reject) => {
            uni.request({
                ...request,
                url: request.baseURL + url,
                data,
                method: 'POST',
                success: (res) => resolve(request.afterResponse(res)),
                fail: reject
            })
        })
    }
}

export default http
