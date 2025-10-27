// Replace mockjs usage with pure JS mock generation

// 简单随机工具
const randInt = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min
const randChoice = (arr) => arr[Math.floor(Math.random() * arr.length)]
const randId = () => Date.now().toString(36) + Math.random().toString(36).slice(2, 8)
const randText = (minWords = 2, maxWords = 6) => {
    const words = ['笔', '书', '手机', '钥匙', '耳机', '笔记本', '杯子', '玩具', '衣服', '手表', '钢笔', '钱包', '充电器']
    const cnt = randInt(minWords, maxWords)
    let s = []
    for (let i = 0; i < cnt; i++) s.push(randChoice(words))
    return s.join('')
}
const randImage = (w = 200, h = 200) => `https://picsum.photos/${w}/${h}?random=${Math.floor(Math.random() * 1000)}`

// 生成 mock 数据
const generateMockData = {
    getUserInfo() {
        return {
            code: 200,
            data: {
                id: randId(),
                nickname: `用户${randInt(1000, 9999)}`,
                avatar: randImage(100, 100),
                stats: {
                    boxCount: randInt(0, 10),
                    itemCount: randInt(10, 100),
                    categoryCount: randInt(5, 15)
                }
            }
        }
    },

    getItems() {
        const total = randInt(12, 24)
        const arr = []
        for (let i = 0; i < total; i++) {
            arr.push({
                id: i + 1,
                itemName: randText(2, 4),
                description: randText(6, 12),
                image: randImage(200, 200),
                boxName: `${randInt(1, 10)}号收纳盒`,
                categoryId: randInt(1, 5),
                createTime: new Date(Date.now() - randInt(0, 1000 * 60 * 60 * 24 * 30)).toISOString()
            })
        }
        return { code: 200, data: arr }
    },

    getBoxes() {
        const n = randInt(3, 8)
        const arr = []
        for (let i = 0; i < n; i++) {
            arr.push({
                id: i + 1,
                boxCode: `BOX${(Math.random().toString(36).toUpperCase().slice(2, 8)).replace(/[^A-Z0-9]/g, 'A')}`,
                boxName: `${randInt(1, 20)}号收纳盒`,
                status: randInt(0, 1),
                rssi: randChoice([-45, -55, -65, -75]),
                battery: randInt(20, 100),
                lastHeartbeatTime: new Date(Date.now() - randInt(0, 1000 * 60 * 60 * 24)).toISOString()
            })
        }
        return { code: 200, data: arr }
    },

    postChat(message) {
        return {
            code: 200,
            data: {
                reply: `已收到：「${String(message).slice(0, 60)}」，为您推荐相关物品。`,
                items: [{
                    id: randId(),
                    itemName: randText(2, 3),
                    boxName: `${randInt(1, 10)}号收纳盒`,
                    image: randImage(120, 120)
                }]
            }
        }
    }
}

// 保留简单 fetch 拦截器（可选），若不想覆盖全局 fetch 可删除此函数
export const setupMock = () => {
    if (typeof window === 'undefined') return
    if (process.env.NODE_ENV === 'development') {
        // 若需要拦截 fetch 使用下面的代理（可注释掉）
        try {
            const originalFetch = window.fetch.bind(window)
            window.fetch = async (url, config = {}) => {
                const key = `${(config.method || 'GET').toUpperCase()} ${url}`
                if (key.includes('/api/user/info')) return new Response(JSON.stringify(generateMockData.getUserInfo()))
                if (key.includes('/api/items')) return new Response(JSON.stringify(generateMockData.getItems()))
                if (key.includes('/api/boxes')) return new Response(JSON.stringify(generateMockData.getBoxes()))
                if (key.includes('/api/chat')) return new Response(JSON.stringify(generateMockData.postChat()))
                return originalFetch(url, config)
            }
        } catch (e) {
            // ignore in restrictive env
        }
    }
}

// 直接导出接口函数（前端直接调用，不走网络）
export const getUserInfo = () => generateMockData.getUserInfo()
export const getItems = (params = {}) => {
    const res = generateMockData.getItems()
    const page = Number(params.page || 1)
    const pageSize = 10
    const all = res.data
    const start = (page - 1) * pageSize
    const pageData = all.slice(start, start + pageSize)
    return { code: 200, data: pageData, total: all.length }
}
export const getBoxes = () => generateMockData.getBoxes()
export const postChat = (message) => generateMockData.postChat(message)
