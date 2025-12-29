// stores/userStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { UserDTO, UserDO, LoginRequest, QueryUserDTO } from '@/common/types';

export const useUserStore = defineStore('user', {
  state: () => ({
    // 当前登录用户
    currentUser: null as UserDTO | null,
    // 认证Token
    token: uni.getStorageSync('token') || '',
    
    // 用户列表 (管理员用)
    userList: [] as UserDTO[],
    pagination: {
      page: 1,
      size: 10,
      total: 0
    },
    
    loading: false,
    isLoggedIn: !!uni.getStorageSync('token')
  }),

  getters: {
    userId: (state) => state.currentUser?.id,
    username: (state) => state.currentUser?.username || state.currentUser?.userAccount,
    isAdmin: (state) => state.currentUser?.userAccount === 'admin' || state.currentUser?.username === 'Admin'
  },

  actions: {
    // 1. 用户登录
    async login(loginData: LoginRequest) {
      this.loading = true;
      try {
        const res = await API.login(loginData);
        // OpenAPI 定义返回为 RestResult_Map，通常包含 token 和 user 对象
        if (res.code === 200 && res.data) {
          // 类型断言：假设 Map 中包含 token (string) 和 user (UserDTO)
          const data = res.data as any; 
          const token = data.token;
          const user = data.user as UserDTO;

          if (token && user) {
            this.token = token;
            this.currentUser = user;
            this.isLoggedIn = true;
            uni.setStorageSync('token', token);
            uni.setStorageSync('userInfo', user); // 可选：持久化用户信息
            return { success: true, user };
          }
        }
        return { success: false, message: res.msg || '登录返回数据异常' };
      } catch (error) {
        console.error('Login error:', error);
        return { success: false, message: '网络请求失败' };
      } finally {
        this.loading = false;
      }
    },

    // 2. 检查登录状态 (初始化调用)
    checkLoginStatus() {
      const token = uni.getStorageSync('token');
      const user = uni.getStorageSync('userInfo');
      if (token && user) {
        this.token = token;
        this.currentUser = user;
        this.isLoggedIn = true;
      } else {
        this.logout();
      }
    },

    // 3. 注册
    async register(userData: UserDO) {
      this.loading = true;
      try {
        const res = await API.registerUser(userData);
        if (res.code === 200) {
          return { success: true, data: res.data };
        }
        return { success: false, message: res.msg };
      } catch (error) {
        return { success: false, message: '注册失败' };
      } finally {
        this.loading = false;
      }
    },

    // 4. 获取当前用户信息
    async fetchUserInfo(id: number) {
        try {
            const res = await API.getUserDetail(id);
            if (res.code === 200) {
                this.currentUser = { ...this.currentUser, ...res.data };
                uni.setStorageSync('userInfo', this.currentUser);
            }
        } catch (e) { console.error(e); }
    },

    // 5. 退出登录
    logout() {
      this.currentUser = null;
      this.isLoggedIn = false;
      this.token = '';
      uni.removeStorageSync('token');
      uni.removeStorageSync('userInfo');
      // 清理其他Store的数据通常在组件中处理，或者引入其他Store在这里reset
    }
  }
});