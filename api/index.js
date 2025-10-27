import { getItems as mockGetItems, getBoxes as mockGetBoxes, getUserInfo as mockGetUserInfo, postChat as mockPostChat } from '@/mock/api'

export const getItems = async (params) => {
    const res = await mockGetItems(params)
    return res.data || res
}

export const getBoxes = async () => {
    const res = await mockGetBoxes()
    return res.data || res
}

export const getUserInfo = async () => {
    const res = await mockGetUserInfo()
    return res.data || res
}

export const sendChatMessage = async (data) => {
    const res = await mockPostChat(data)
    return res.data || res
}
