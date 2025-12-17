import { defineStore } from 'pinia';

import type { UserDTO } from '@/common/types';

export const useUserStore = defineStore('user', {
  // 1. 定义状态 (State)
  state: () => ({
    // 从本地存储初始化，防止刷新丢失
    token: uni.getStorageSync('token') as string || '',
    userInfo: uni.getStorageSync('userInfo') as UserDTO | null,
  }),

  // 2. 计算属性 (Getters)
  getters: {
    isLoggedIn: (state) => !!state.token,
    userName: (state) => state.userInfo?.username || '未登录用户'
  },

  // 3. 操作方法 (Actions)
  actions: {
    // 登录成功
    login(token: string, user: UserDTO) {
      this.token = token;
      this.userInfo = user;
      
      // 持久化
      uni.setStorageSync('token', token);
      uni.setStorageSync('userInfo', user);
    },

    // 退出登录
    logout() {
      this.token = '';
      this.userInfo = null;
      
      // 清除持久化
      uni.removeStorageSync('token');
      uni.removeStorageSync('userInfo');
      
      // 跳转登录页
      uni.reLaunch({ url: '/pages/user/login' });
    }
  }
});