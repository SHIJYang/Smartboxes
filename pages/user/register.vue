<template>
  <view class="login-container">
    <view class="bubble bubble-1"></view>
    <view class="bubble bubble-2"></view>
    <view class="bubble bubble-3"></view>

    <view class="content-box">
      <view class="header fade-in-down">
        <text class="app-name">新用户注册</text>
        <text class="sub-title">Join Smart Box System</text>
      </view>

      <view class="card fade-in-up">
        <view class="input-group" :class="{ 'input-focus': focusField === 'account' }">
          <view class="icon-box">👤</view>
          <input 
            class="inp" 
            v-model="formData.userAccount" 
            type="text" 
            placeholder="设置账号" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'account'"
            @blur="focusField = ''"
          />
        </view>

        <view class="input-group" :class="{ 'input-focus': focusField === 'username' }">
          <view class="icon-box">🏷️</view>
          <input 
            class="inp" 
            v-model="formData.username" 
            type="text" 
            placeholder="您的昵称" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'username'"
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
            placeholder="设置密码" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'password'"
            @blur="focusField = ''"
          />
        </view>

        <view class="input-group" :class="{ 'input-focus': focusField === 'confirmPwd' }">
          <view class="icon-box">🛡️</view>
          <input 
            class="inp" 
            v-model="confirmPassword" 
            type="safe-password" 
            password
            placeholder="确认密码" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'confirmPwd'"
            @blur="focusField = ''"
          />
        </view>
        
        <button 
          class="btn-login" 
          hover-class="btn-hover" 
          @click="handleRegister" 
          :loading="submitting"
          :disabled="submitting"
        >
          {{ submitting ? '注册中...' : '立 即 注 册' }}
        </button>

        <view class="footer-links">
          <text class="link-text" @click="goLogin">已有账号？直接登录</text>
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
import { registerUser } from '@/api/index';
import type { UserDO } from '@/common/types';

// 表单数据
const formData = reactive<UserDO>({
  userAccount: '',
  userPassword: '',
  username: '',
  phone: ''
});

const confirmPassword = ref('');
const submitting = ref(false);
const focusField = ref('');

const handleRegister = async () => {
  // 1. 校验
  if (!formData.userAccount || !formData.userPassword || !formData.username) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' });
    return;
  }
  if (formData.userPassword !== confirmPassword.value) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' });
    return;
  }
  
  submitting.value = true;
  
  try {
    // 2. 调用API
    const res = await registerUser(formData);

    if (res.code === 200) {
      uni.showToast({ title: '注册成功', icon: 'success' });
      // 延迟返回登录页
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    } else {
      uni.showToast({ title: res.msg || '注册失败', icon: 'none' });
    }
  } catch (error) {
    console.error('Register failed:', error);
    uni.showToast({ title: '网络异常', icon: 'none' });
  } finally {
    submitting.value = false;
  }
};

const goLogin = () => {
  uni.navigateBack();
};
</script>

<style lang="scss" scoped>
$primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$btn-gradient: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
$glass-bg: rgba(255, 255, 255, 0.80);
$text-color: #333;

.login-container {
  min-height: 100vh;
  /* 更高级的深邃渐变背景 */
  background: linear-gradient(to top, #dfe9f3 0%, white 100%); 
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 气泡动画 - 颜色调整得更柔和 */
.bubble {
  position: absolute;
  border-radius: 50%;
  filter: blur(50px);
  z-index: 0;
  animation: float 8s infinite ease-in-out;
}
.bubble-1 { width: 300rpx; height: 300rpx; background: rgba(161, 140, 209, 0.3); top: -50rpx; left: -50rpx; }
.bubble-2 { width: 250rpx; height: 250rpx; background: rgba(251, 194, 235, 0.3); bottom: 100rpx; right: -60rpx; animation-delay: -3s; }
.bubble-3 { width: 150rpx; height: 150rpx; background: rgba(168, 237, 234, 0.3); top: 30%; left: 80%; animation-delay: -5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(20px); }
}

.content-box { width: 100%; padding: 0 60rpx; z-index: 10; }

.header {
  text-align: center;
  margin-bottom: 60rpx;
  
  .logo-box {
    width: 140rpx; height: 140rpx;
    background: #fff;
    border-radius: 40rpx;
    margin: 0 auto 30rpx;
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 15rpx 35rpx rgba(0,0,0,0.08);
    animation: float 6s infinite ease-in-out;
    .logo-icon { font-size: 70rpx; }
  }
  
  .app-name { font-size: 48rpx; font-weight: 800; color: #333; letter-spacing: 2rpx; display: block; }
  .sub-title { font-size: 24rpx; color: #999; letter-spacing: 4rpx; margin-top: 10rpx; text-transform: uppercase; }
}

.card {
  background: $glass-bg;
  backdrop-filter: blur(20px);
  border-radius: 40rpx;
  padding: 50rpx 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(0,0,0,0.05);
  border: 1px solid rgba(255,255,255,0.6);
  
  .welcome-row {
    margin-bottom: 50rpx;
    .h1 { font-size: 40rpx; font-weight: bold; color: $text-color; display: block; }
    .h2 { font-size: 26rpx; color: #999; margin-top: 10rpx; display: block; }
  }
}

.input-group {
  display: flex; align-items: center;
  background: #f5f7fa;
  border-radius: 30rpx;
  padding: 0 30rpx;
  margin-bottom: 30rpx;
  height: 100rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s;
  
  .icon-wrapper { font-size: 36rpx; margin-right: 24rpx; opacity: 0.5; }
  .inp { flex: 1; height: 100%; font-size: 30rpx; color: $text-color; }
  
  &.input-focus {
    background: #fff;
    border-color: #4facfe;
    box-shadow: 0 0 15rpx rgba(79, 172, 254, 0.15);
    .icon-wrapper { opacity: 1; transform: scale(1.1); transition: all 0.2s; }
  }
}

.btn-login {
  margin-top: 60rpx;
  height: 100rpx; line-height: 100rpx;
  background: $btn-gradient;
  color: #fff; font-size: 34rpx; font-weight: bold;
  border-radius: 50rpx;
  box-shadow: 0 15rpx 30rpx rgba(79, 172, 254, 0.3);
  
  &::after { border: none; }
  &[disabled] { opacity: 0.7; filter: grayscale(0.8); }
}
.btn-hover { transform: scale(0.98); opacity: 0.9; }

.footer-links {
  margin-top: 40rpx;
  display: flex; justify-content: center; align-items: center;
  .link-text { font-size: 26rpx; color: #666; padding: 10rpx 20rpx; }
  .divider { color: #ddd; }
}

.copyright { position: absolute; bottom: 40rpx; font-size: 22rpx; color: #999; opacity: 0.6; }

.fade-in-down { animation: fadeInDown 0.8s ease-out; }
.fade-in-up { animation: fadeInUp 0.8s ease-out; }
@keyframes fadeInDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
</style>