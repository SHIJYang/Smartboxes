<template>
  <view class="login-container">
    <view class="bubble bubble-1"></view>
    <view class="bubble bubble-2"></view>
    <view class="bubble bubble-3"></view>

    <view class="content-box">
      <view class="header fade-in-down">
        <view class="logo-box">
          <text class="logo-icon">📦</text>
        </view>
        <text class="app-name">智能收纳盒</text>
        <text class="sub-title">Smart Box System</text>
      </view>

      <view class="card fade-in-up">
        <view class="welcome-text">欢迎回来</view>

        <view class="input-group" :class="{ 'input-focus': focusField === 'account' }">
          <view class="icon-box">👤</view>
          <input 
            class="inp" 
            v-model="formData.userAccount" 
            type="text" 
            placeholder="请输入账号" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'account'"
            @blur="focusField = ''"
          />
        </view>
        
        <view class="input-group" :class="{ 'input-focus': focusField === 'password' }">
          <view class="icon-box">🔒</view>
          <input 
            class="inp" 
            v-model="formData.userPassword" 
            type="safe-password" 
            password
            placeholder="请输入密码" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'password'"
            @blur="focusField = ''"
            @confirm="handleLogin"
          />
        </view>
        
        <button 
          class="btn-login" 
          hover-class="btn-hover" 
          @click="handleLogin" 
          :loading="loggingIn"
          :disabled="loggingIn"
        >
          {{ loggingIn ? '登录中...' : '立 即 登 录' }}
        </button>

        <view class="footer-links">
          <text class="link-text">注册账号</text>
          <text class="divider">|</text>
          <text class="link-text">忘记密码?</text>
        </view>
      </view>
    </view>
    
    <view class="copyright">
      © 2024 Smart Storage Corp.
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { login } from '@/api'; 
import type { LoginRequest } from '@/common/types';
import { useUserStore } from '@/stores/user';

// 表单数据
const formData = reactive<LoginRequest>({
  userAccount: '',
  userPassword: ''
});

// UI 状态
const loggingIn = ref(false);
const focusField = ref(''); // 当前聚焦的输入框

// 初始化 Store
const userStore = useUserStore();

const handleLogin = async () => {
  // 1. 基础校验
  if (!formData.userAccount || !formData.userPassword) {
    uni.showToast({ title: '请输入账号和密码', icon: 'none' });
    return;
  }
  
  loggingIn.value = true;
  
  try {
    // 2. 调用 API
    const res = await login(formData);

    // 3. 处理业务逻辑
    if (res.code === 200) {
      const resultData = res.data as any; 
      
      // --- 数据适配逻辑 ---
      // 兼容 Mock 数据 (mockUser 只返回了 user 信息，没有 token)
      // 如果后端没有返回 token，我们在前端模拟一个，保证流程通畅
      const token = resultData.token || 'mock-token-' + Date.now();
      
      // 如果后端直接返回了 User 对象 (如 mockUser)，则直接使用
      // 如果后端结构是 { user: {...}, token: ... }，则取 resultData.user
      const userInfo = resultData.username ? resultData : (resultData.user || {
        id: 0,
        username: formData.userAccount,
        email: 'loading...'
      });

      // 4. 更新 Store (Pinia 会自动处理持久化)
      userStore.login(token, userInfo);
      
      uni.showToast({ title: '登录成功', icon: 'success' });
      
      // 延迟跳转，提升体验
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' });
      }, 800);
      
    } else {
      // 业务错误 (如密码错误)
      // request.ts 通常会弹窗，这里只重置密码
      formData.userPassword = '';
    }
  } catch (error) {
    console.error('Login failed:', error);
    // request.ts 已处理网络异常提示
  } finally {
    loggingIn.value = false;
  }
};
</script>

<style lang="scss" scoped>
/* 变量定义 */
$primary-color: #4facfe;
$secondary-color: #00f2fe;
$text-color: #333;
$placeholder-color: #999;
$glass-bg: rgba(255, 255, 255, 0.85);

.login-container {
  min-height: 100vh;
  /* 更加清新的渐变背景 */
  background: linear-gradient(120deg, #e0c3fc 0%, #8ec5fc 100%);
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 背景气泡动画 */
.bubble {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  z-index: 1;
  animation: float 10s infinite ease-in-out;
}
.bubble-1 { width: 250px; height: 250px; background: rgba(79, 172, 254, 0.4); top: -80px; left: -80px; animation-delay: 0s; }
.bubble-2 { width: 200px; height: 200px; background: rgba(224, 195, 252, 0.5); bottom: 100px; right: -50px; animation-delay: -3s; }
.bubble-3 { width: 150px; height: 150px; background: rgba(255, 255, 255, 0.3); top: 40%; left: 20%; animation-delay: -5s; }

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(20px) scale(1.1); }
}

.content-box {
  width: 100%;
  padding: 0 50rpx;
  z-index: 10;
}

/* 头部样式 */
.header {
  text-align: center;
  margin-bottom: 50rpx;
  
  .logo-box {
    width: 120rpx;
    height: 120rpx;
    background: #fff;
    border-radius: 35rpx;
    margin: 0 auto 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 10rpx 25rpx rgba(0,0,0,0.1);
    
    .logo-icon { font-size: 60rpx; }
  }
  
  .app-name {
    font-size: 44rpx;
    font-weight: 700;
    color: #fff;
    text-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
    display: block;
  }
  
  .sub-title {
    font-size: 24rpx;
    color: rgba(255,255,255,0.9);
    letter-spacing: 2rpx;
    margin-top: 10rpx;
    display: block;
  }
}

/* 卡片样式 */
.card {
  background: $glass-bg;
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  max-width: 380rpx;
  border-radius: 40rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 30rpx 60rpx rgba(0,0,0,0.08);
  border: 1px solid rgba(255,255,255,0.6);
  
  .welcome-text {
    font-size: 36rpx;
    font-weight: bold;
    color: $text-color;
    margin-bottom: 40rpx;
    padding-left: 10rpx;
  }
}

/* 输入框组 */
.input-group {
  display: flex;
  align-items: center;
  background: #f7f9fc;
  border-radius: 25rpx;
  padding: 0 30rpx;
  margin-bottom: 30rpx;
  height: 100rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;
  
  .icon-box {
    margin-right: 20rpx;
    font-size: 32rpx;
    color: #999;
  }
  
  .inp {
    flex: 1;
    height: 100%;
    font-size: 30rpx;
    color: $text-color;
  }
  
  // 聚焦状态
  &.input-focus {
    background: #fff;
    border-color: $primary-color;
    box-shadow: 0 4rpx 15rpx rgba(79, 172, 254, 0.2);
    
    .icon-box { color: $primary-color; }
  }
}

.placeholder-style {
  color: #bbb;
}

/* 按钮样式 */
.btn-login {
  margin-top: 50rpx;
  height: 100rpx;
  line-height: 100rpx;
  background: linear-gradient(90deg, $primary-color, $secondary-color);
  color: #fff;
  font-size: 34rpx;
  font-weight: bold;
  border-radius: 50rpx;
  box-shadow: 0 15rpx 30rpx rgba(79, 172, 254, 0.4);
  transition: all 0.3s;
  
  &::after { border: none; }
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 5rpx 15rpx rgba(79, 172, 254, 0.3);
  }
  
  &[disabled] {
    opacity: 0.7;
    filter: grayscale(0.5);
  }
}

.footer-links {
  margin-top: 40rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  
  .link-text {
    font-size: 26rpx;
    color: #666;
    padding: 10rpx 20rpx;
  }
  
  .divider {
    color: #ddd;
    margin: 0 10rpx;
  }
}

.copyright {
  position: absolute;
  bottom: 40rpx;
  font-size: 22rpx;
  color: #fff;
  opacity: 0.6;
}

/* 进场动画 */
.fade-in-down {
  animation: fadeInDown 0.8s ease-out;
}
.fade-in-up {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>