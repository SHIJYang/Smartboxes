import { defineStore } from 'pinia'
import axios from '../axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    currentUser: null,
    token: localStorage.getItem('token') || null
  }),
  actions: {
    async login(userAccount, userPassword) {
      try {
        const response = await axios.post('/api/users/login', {
          userAccount,
          userPassword
        })
        this.token = response.data.token
        localStorage.setItem('token', this.token)
        return response
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },
    logout() {
      this.token = null
      this.currentUser = null
      localStorage.removeItem('token')
    },
    async getUserInfo() {
      try {
        const response = await axios.get('/api/users/me')
        this.currentUser = response.data
        return response
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    }
  }
})