import request from '@/utils/request'

export const getItems = (params) => {
    return request.get('/api/items', { params })
}

export const getBoxes = () => {
    return request.get('/api/boxes')
}

export const getUserInfo = () => {
    return request.get('/api/user/info')
}

export const sendChatMessage = (data) => {
    return request.post('/api/chat', data)
}
