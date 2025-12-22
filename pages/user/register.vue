<template>
  <view class="login-container">
    <view class="bubble bubble-1"></view>
    <view class="bubble bubble-2"></view>
    <view class="bubble bubble-3"></view>

    <view class="content-box">
      <view class="header fade-in-down">
        <text class="app-name">加入魔法世界</text>
        <text class="sub-title">Create New Account</text>
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
            placeholder="怎么称呼你?" 
            placeholder-class="placeholder-style"
            @focus="focusField = 'username'"
            @blur="focusField = ''"
          />
        </view>
        
        <view class="input-group" :class="{ 'input-focus': focusField === 'password' }">
          <view class="icon-box">🔐</view>
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
          {{ submitting ? '创建中...' : '立 即 注 册' }}
        </button>

        <view class="footer-links">
          <text class="link-text" @click="goLogin">已有账号？返回登录</text>
        </view>
      </view>
    </view>
    
    <view class="copyright">
      © 2025 Magic Storage Corp.
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { registerUser } from '@/api/index';
import type { UserDO } from '@/common/types';

const formData = reactive<UserDO>({ userAccount: '', userPassword: '', username: '', phone: '' });
const confirmPassword = ref('');
const submitting = ref(false);
const focusField = ref('');

const handleRegister = async () => {
  if (!formData.userAccount || !formData.userPassword || !formData.username) {
    uni.showToast({ title: '请把信息填满哦', icon: 'none' });
    return;
  }
  if (formData.userPassword !== confirmPassword.value) {
    uni.showToast({ title: '两次密码对不上', icon: 'none' });
    return;
  }
  
  submitting.value = true;
  try {
    const res = await registerUser(formData);
    if (res.code === 200) {
      uni.showToast({ title: '注册成功!', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: res.msg || '失败了', icon: 'none' });
    }
  } catch (error) {
    uni.showToast({ title: '网络开小差了', icon: 'none' });
  } finally {
    submitting.value = false;
  }
};

const goLogin = () => uni.navigateBack();
</script>

<style lang="scss" scoped>
/* 与 Login 保持一致的暖色变量 */
$bg-color: #FFF9F0;
$btn-gradient: linear-gradient(120deg, #ff9a9e 0%, #fecfef 100%); /* 粉色渐变 */
$primary-pink: #FF9A9E;

.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF9F0 0%, #FFF0F5 100%);
  position: relative;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  overflow: hidden;
}

.bubble {
  position: absolute; border-radius: 50%; filter: blur(60px); z-index: 0;
  animation: float 8s infinite ease-in-out;
}
.bubble-1 { width: 400rpx; height: 400rpx; background: rgba(255, 154, 158, 0.2); top: -100rpx; left: -100rpx; }
.bubble-2 { width: 300rpx; height: 300rpx; background: rgba(161, 140, 209, 0.2); bottom: 50rpx; right: -80rpx; animation-delay: -3s; }
.bubble-3 { width: 200rpx; height: 200rpx; background: rgba(255, 215, 0, 0.15); top: 20%; right: 10%; animation-delay: -5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(30px); }
}

.content-box { width: 100%; padding: 0 60rpx; z-index: 10; }

.header {
  text-align: center; margin-bottom: 60rpx;
  .app-name { font-size: 48rpx; font-weight: 900; color: #333; letter-spacing: 2rpx; display: block; }
  .sub-title { font-size: 24rpx; color: #999; letter-spacing: 4rpx; margin-top: 10rpx; text-transform: uppercase; }
}

.card {
  background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(25px);
  border-radius: 40rpx; padding: 50rpx 40rpx;
  box-shadow: 0 20rpx 60rpx rgba(255, 154, 158, 0.15);
  border: 2px solid #fff;
}

.input-group {
  display: flex; align-items: center;
  background: #FFF5F7;
  border-radius: 30rpx; padding: 0 30rpx; margin-bottom: 30rpx;
  height: 100rpx; border: 2rpx solid transparent; transition: all 0.3s