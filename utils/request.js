// 使用本地 mock 数据替代真实请求

import { getItems, getUserInfo, getBoxes, postChat } from '../mock/api'

const http = {
    async get(url, params = {}) {
        try {
            if (url === '/items' || url.startsWith('/items')) {
                const res = await getItems(params)
                return res.data || res
            }
            if (url === '/user/info' || url.endsWith('/user/info')) {
                const res = await getUserInfo()
                return res.data || res
            }
            if (url === '/boxes' || url.startsWith('/boxes')) {
                const res = await getBoxes()
                return res.data || res
            }
            return null
        } catch (e) {
            return Promise.reject(e)
        }
    },
    async post(url, data = {}) {
        try {
            if (url === '/chat' || url.endsWith('/chat')) {
                const res = await postChat(data)
                return res.data || res
            }
            return null
        } catch (e) {
            return Promise.reject(e)
        }
    }
}

export default http
