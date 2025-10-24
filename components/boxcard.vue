<template>
  <u-card
    :border="false"
    :margin="margin"
    :padding="padding"
    @click="handleClick"
    class="box-card"
    :class="{ 'box-card--active': isActive }"
  >
    <view class="card-content">
      <!-- 图标区域 -->
      <view class="icon-box">
        <view class="icon-frame"></view>
        <u-icon
          :name="getIcon(category)"
          size="48"
          color="#3B82F6"
        />
      </view>

      <!-- 文字信息 -->
      <view class="info">
        <text class="name">{{ name }}</text>
        <text class="desc">{{ itemCount }} 件物品 · {{ location }}</text>
        <text class="time">最后整理：{{ formatDate(lastModified) }}</text>
      </view>

      <!-- 电池状态 -->
      <view v-if="showBattery" class="battery-badge" :class="{ 'low-battery': isLowBattery }">
        <u-icon
          :name="isCharging ? 'battery-charging' : 'battery'"
          size="18"
          :color="isLowBattery && !isCharging ? '#EF4444' : '#3B82F6'"
        />
        <text class="battery-text">{{ batteryLevel }}%</text>
      </view>

      <!-- 右侧箭头 -->
      <view class="arrow-wrapper">
        <view class="arrow-line"></view>
        <u-icon name="arrow-right" color="#3B82F6" size="24" />
      </view>
    </view>
  </u-card>
</template>

<script setup>
// 逻辑保持不变
import { ref, computed } from 'vue'

const props = defineProps({
  name: String,
  category: { type: String, default: 'box' },
  itemCount: { type: Number, default: 0 },
  location: String,
  lastModified: [String, Date],
  batteryLevel: { type: Number, default: 0 },
  isCharging: { type: Boolean, default: false },
  border: { type: Boolean, default: false },
  margin: { type: [String, Number], default: '24rpx' },
  padding: { type: [String, Number], default: '32rpx' }
})

const emit = defineEmits(['click'])

const showBattery = computed(() => props.batteryLevel !== null && props.batteryLevel >= 0)
const isLowBattery = computed(() => {
  return showBattery.value && !props.isCharging && props.batteryLevel < 20
})

const isActive = ref(false)

const getIcon = (cat) => {
  const icons = {
    box: 'inbox',
    drawer: 'layers',
    shelf: 'book',
    wardrobe: 'tshirt',
    default: 'cube'
  }
  return icons[cat] || icons.default
}

const formatDate = (date) => {
  if (!date) return '暂无记录'
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const handleClick = () => {
  if (uni.vibrateShort) uni.vibrateShort()
  isActive.value = true
  emit('click')
  setTimeout(() => {
    isActive.value = false
  }, 150)
}
</script>

<style scoped>
.box-card {
  background-color: #fff;
  border-radius: 16rpx;
  border: 1rpx solid #e5e7eb;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.03);
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
}

/* 科技感点击效果 */
.box-card--active {
  border-color: #93c5fd;
  box-shadow: 0 4rpx 20rpx rgba(59, 130, 246, 0.1);
}

.box-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4rpx;
  height: 100%;
  background-color: #3B82F6;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.box-card--active::before {
  opacity: 1;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 32rpx;
  position: relative;
  min-height: 150rpx;
}

/* 图标区域 - 科技感框架 */
.icon-box {
  width: 100rpx;
  height: 100rpx;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-frame {
  position: absolute;
  width: 80rpx;
  height: 80rpx;
  border: 2rpx solid #dbeafe;
  border-radius: 12rpx;
  transform: rotate(45deg);
  transition: all 0.2s ease;
}

.box-card--active .icon-frame {
  background-color: #eff6ff;
  transform: rotate(45deg) scale(1.1);
}

.icon-box u-icon {
  position: relative;
  z-index: 1;
}

/* 文字信息 */
.info {
  flex: 1;
  min-width: 0;
}

.name {
  font-size: 34rpx;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.desc {
  font-size: 26rpx;
  color: #64748b;
  margin: 6rpx 0;
  line-height: 1.4;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time {
  font-size: 22rpx;
  color: #94a3b8;
  display: block;
}

/* 电池状态 */
.battery-badge {
  position: absolute;
  top: 24rpx;
  right: 80rpx;
  display: flex;
  align-items: center;
  gap: 6rpx;
  background-color: #f8fafc;
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
  font-size: 20rpx;
  color: #3B82F6;
  border: 1rpx solid #dbeafe;
}

.low-battery {
  background-color: #fef2f2;
  border-color: #fee2e2;
  color: #EF4444;
}

/* 右侧箭头区域 */
.arrow-wrapper {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-left: auto;
  padding-left: 20rpx;
  position: relative;
}

.arrow-line {
  width: 30rpx;
  height: 1rpx;
  background-color: #dbeafe;
  transition: all 0.2s ease;
}

.box-card--active .arrow-line {
  background-color: #93c5fd;
  width: 40rpx;
}
</style>