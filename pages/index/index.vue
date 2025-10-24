<template>
  <view class="container">
    <u-navbar title="我的收纳空间" title-bold title-color="#333" />

    <u-empty
      v-if="spaceList.length === 0 && !loading"
      text="暂无收纳空间"
      mode="data"
      :show="true"
      class="empty-state"
    >
      <u-button type="primary" size="mini" @click="addSpace">立即创建</u-button>
    </u-empty>

    <u-loading
      v-else-if="loading"
      mode="circle"
      size="40"
      color="#4A90E2"
      vertical
      text="加载中..."
      class="loading-state"
    />

    <!-- 关键：加上 enable-flex -->
    <scroll-view
      v-else
      scroll-y
      enable-flex
      class="space-list"
      :style="{ height: listHeight + 'px' }"
    >
      <boxcard
        v-for="space in spaceList"
        :key="space.id"
        :name="space.name"
        :category="space.category"
        :item-count="space.itemCount"
        :location="space.location"
        :last-modified="space.lastModified"
        :battery-level="space.batteryLevel"
        :is-charging="space.isCharging"
        @click="gotoSpace(space.id)"
      />
    </scroll-view>

    <u-fab icon="plus" position="bottomRight" :offset="[40, 80]" @click="addSpace" />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchSpaces } from '../../api/mock/spaces.js'
import boxcard from '@/components/boxcard.vue'

const spaceList = ref([])
const loading = ref(false)
const listHeight = ref(0)

onMounted(() => {
  const sysInfo = uni.getSystemInfoSync()
  // 更准确的高度计算
  const navHeight = (sysInfo.statusBarHeight || 0) + (sysInfo.platform === 'ios' ? 44 : 48)
  listHeight.value = sysInfo.windowHeight - navHeight - 60 // 60px 预留底部
  console.log('列表高度:', listHeight.value) // 调试用
})

const loadData = async () => {
  loading.value = true
  try {
    const data = await fetchSpaces()
    spaceList.value = data
  } catch (err) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const gotoSpace = (id) => {
  uni.navigateTo({ url: `/pages/space/space?id=${id}` })
}

const addSpace = () => {
  uni.showToast({ title: '功能开发中', icon: 'none' })
}
</script>

<style scoped>
.container {
  background-color: #f8f9fa;
  min-height: 100vh;
  position: relative;
}

.space-list {
  padding: 20rpx;
  box-sizing: border-box;
}
</style>