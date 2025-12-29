{
type: uploaded file
fileName: register.vue
fullContent:
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
import type { UserDO } from '@/common/types';
import { useUserStore } from '@/stores';

const userStore = useUserStore();

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
    const result = await userStore.register(formData);
    
    if (result.success) {
      uni.showToast({ title: '注册成功!', icon: 'success' });
      // Redirect to login or auto-login logic
      setTimeout(() => uni.navigateBack(), 1500);
    } else {
      uni.showToast({ title: result.message || '注册失败', icon: 'none' });
    }
  } catch (error) {
    uni.showToast({ title: '网络开小差了', icon: 'none' });
    console.error('Registration error:', error);
  } finally {
    submitting.value = false;
  }
};

const goLogin = () => uni.navigateBack();
</script>

<style lang="scss" scoped>
$bg-color: #FFF9F0;
$btn-gradient: linear-gradient(120deg, #ff9a9e 0%, #fecfef 100%);
$primary-pink: #FF9A9E;

.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF9F0 0%, #FFF0F5 100%);
  position: relative;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  overflow: hidden;
  padding: 40rpx 0;
}

.bubble {
  position: absolute; border-radius: 50%; filter: blur(60px); z-index: 0;
  animation: float 8s infinite ease-in-out;
}
.bubble-1 { width: 400rpx; height: 400rpx; background: rgba(255, 154, 158, 0.2); top: -100rpx; left: -100rpx; }
.bubble-2 { width: 300rpx; height: 300rpx; background: rgba(161, 140, 209, 0.2); bottom: 50rpx; right: -80rpx; animation-delay: -3s; }
.bubble-3 { width: 200rpx; height: 200rpx; background: rgba(255, 215, 0, 0.15); top: 20%; right: 10%; animation-delay: -5s; }

@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(30px); } }

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
  height: 100rpx; border: 2rpx solid transparent; transition: all 0.3s ease;
  
  &.input-focus { background: #fff; border-color: $primary-pink; box-shadow: 0 6rpx 20rpx rgba(255, 154, 158, 0.2); }
  
  .icon-box { font-size: 38rpx; color: $primary-pink; margin-right: 20rpx; width: 50rpx; text-align: center; }
  
  .inp {
    flex: 1; font-size: 30rpx; color: #333; height: 100rpx; line-height: 100rpx;
    background: transparent; border: none; outline: none;
    &::placeholder { color: #ccc; font-size: 28rpx; }
  }
}

.btn-login {
  width: 100%; height: 100rpx; line-height: 100rpx; border-radius: 50rpx;
  background: $btn-gradient; color: #fff; font-size: 34rpx; font-weight: 800;
  margin-top: 20rpx; margin-bottom: 40rpx; border: none; position: relative;
  overflow: hidden; z-index: 1;
  &::after { border: none; }
  &::before {
    content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%;
    background: linear-gradient(120deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.3));
    transition: all 0.6s ease; z-index: -1;
  }
  &:hover::before, &.btn-hover::before { left: 100%; }
  &:disabled, &[loading] { opacity: 0.7; }
}

.footer-links {
  text-align: center; margin-top: 30rpx;
  .link-text {
    color: $primary-pink; font-size: 28rpx; text-decoration: none;
    position: relative; display: inline-block; padding: 10rpx 0;
    &::after {
      content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
      width: 0; height: 2rpx; background: $primary-pink; transition: all 0.3s ease;
    }
    &:hover::after, &:active::after { width: 100%; }
  }
}

.copyright {
  position: absolute; bottom: 40rpx; left: 0; right: 0; text-align: center;
  font-size: 24rpx; color: #ccc; z-index: 10;
}

.fade-in-down { animation: fadeInDown 0.6s ease-out; }
.fade-in-up { animation: fadeInUp 0.6s ease-out; }

@keyframes fadeInDown { from { opacity: 0; transform: translateY(-30px); } to { opacity: 1; transform: translateY(0); } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

.placeholder-style { color: #ccc; font-size: 28rpx; }
@media screen and (min-width: 768px) { .content-box { width: 600rpx; margin: 0 auto; } .card { padding: 60rpx 50rpx; } }
</style>
}