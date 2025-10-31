<template>
  <view class="page">
    <u-navbar title="隐私设置" />
    <u-cell-group>
      <u-cell title="允许个性化推荐">
        <template #value>
          <u-switch v-model="form.personalized" />
        </template>
      </u-cell>
      <u-cell title="允许收集使用数据">
        <template #value>
          <u-switch v-model="form.analytics" />
        </template>
      </u-cell>
      <u-cell title="展示在线状态">
        <template #value>
          <u-switch v-model="form.online" />
        </template>
      </u-cell>
    </u-cell-group>
    <view class="footer">
      <u-button type="primary" @click="save">保存</u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const form = ref({ personalized: true, analytics: false, online: true })

const save = () => {
  // TODO: 持久化到本地或服务端
  uni.showToast({ title: '已保存' })
  setTimeout(() => uni.navigateBack(), 600)
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f7f8fa; }
.footer { padding: 32rpx; }
</style>
