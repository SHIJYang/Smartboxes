// stores/userStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { UserDTO, UserDO, LoginRequest, UserQuery } from '@/common/types';

export const useUserStore = defineStore('user', {
  state: () => ({
    // 当前登录用户信息
    currentUser: null as UserDTO | null,
    
    // 用户列表（用于管理页面）
    userList: [] as UserDTO[],
    
    // 分页信息
    pagination: {
      currentPage: 1,
      pageSize: 10,
      total: 0
    },
    
    // 加载状态
    loading: false,
    
    // 登录状态
    isLoggedIn: false,
    
    // token
    token: localStorage.getItem('token') || ''
  }),

  getters: {
    // 获取当前用户ID
    userId: (state) => state.currentUser?.id,
    
    // 获取用户名
    username: (state) => state.currentUser?.username,
    
    // 是否管理员（根据业务逻辑调整）
    isAdmin: (state) => state.currentUser?.username === 'admin'
  },

  actions: {
    // 用户登录
    async login(loginData: LoginRequest) {
      this.loading = true;
      try {
        const response = await API.login(loginData);
        
        // 假设响应中包含token和用户信息
        if (response.data && response.data.token) {
          this.token = response.data.token;
          this.currentUser = response.data.user;
          this.isLoggedIn = true;
          
          // 存储token到localStorage
          localStorage.setItem('token', response.data.token);
          
          return { success: true, data: response.data };
        }
        
        return { success: false, message: '登录失败' };
      } catch (error) {
        console.error('登录失败:', error);
        return { success: false, message: '登录失败，请检查账号密码' };
      } finally {
        this.loading = false;
      }
    },

    // 用户注册
    async register(userData: UserDO) {
      this.loading = true;
      try {
        const response = await API.registerUser(userData);
        return { success: true, data: response.data };
      } catch (error) {
        console.error('注册失败:', error);
        return { success: false, message: '注册失败' };
      } finally {
        this.loading = false;
      }
    },

    // 获取用户信息
    async fetchUserInfo(userId: number) {
      this.loading = true;
      try {
        const response = await API.getUserInfo(userId);
        this.currentUser = response.data;
        return response.data;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 获取用户列表（管理功能）
    async fetchUserList(params?: UserQuery) {
      this.loading = true;
      try {
        // 这里需要对应的API接口，根据实际情况调整
        // const response = await API.getUserList(params);
        // this.userList = response.data.list || response.data;
        // this.pagination = {
        //   currentPage: response.data.currentPage || 1,
        //   pageSize: response.data.pageSize || 10,
        //   total: response.data.total || 0
        // };
        
        // 模拟数据
        this.userList = [
          { id: 1, username: 'admin', email: 'admin@example.com', createdAt: '2023-01-01', updatedAt: '2023-01-01' },
          { id: 2, username: 'user1', email: 'user1@example.com', createdAt: '2023-01-02', updatedAt: '2023-01-02' }
        ];
        
        return this.userList;
      } catch (error) {
        console.error('获取用户列表失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 更新用户信息
    async updateUserInfo(userData: Partial<UserDTO>) {
      this.loading = true;
      try {
        // 这里需要对应的API接口
        // await API.updateUser(userData);
        
        if (this.currentUser) {
          this.currentUser = { ...this.currentUser, ...userData };
        }
        
        return { success: true };
      } catch (error) {
        console.error('更新用户信息失败:', error);
        return { success: false, message: '更新失败' };
      } finally {
        this.loading = false;
      }
    },

    // 用户登出
    logout() {
      this.currentUser = null;
      this.isLoggedIn = false;
      this.token = '';
      localStorage.removeItem('token');
      // 可以重定向到登录页
      window.location.href = '/login';
    },

    // 检查登录状态
    checkLoginStatus() {
      const token = localStorage.getItem('token');
      if (token) {
        this.token = token;
        this.isLoggedIn = true;
        // 可以自动获取用户信息
        // this.fetchUserInfo(userId);
      }
    }
  }
});