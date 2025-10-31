<template>
  <view class="page">
    <u-navbar title="修改密码" />
    <view class="form">
      <u-input v-model="form.oldPwd" placeholder="原密码" type="password" />
      <u-input v-model="form.newPwd" placeholder="新密码" type="password" />
      <u-input v-model="form.confirmPwd" placeholder="确认新密码" type="password" />
      <u-button type="primary" @click="submit">提交</u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const form = ref({ oldPwd: '', newPwd: '', confirmPwd: '' })

const submit = () => {
  if (!form.value.oldPwd || !form.value.newPwd || !form.value.confirmPwd) {
    return uni.showToast({ title: '请填写完整', icon: 'none' })
  }
  if (form.value.newPwd !== form.value.confirmPwd) {
    return uni.showToast({ title: '两次密码不一致', icon: 'none' })
  }
  // TODO: 调用修改密码接口
  uni.showToast({ title: '修改成功' })
  setTimeout(() => uni.navigateBack(), 800)
}
</script>

<style scoped>
.page { padding: 24rpx; }
.form { display: grid; gap: 24rpx; margin-top: 24rpx; }
</style>
