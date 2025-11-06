<template>
  <u-card
    :border="false"
    :margin="margin"
    :padding="padding"
    @click="handleClick"
    class="box-card"
    :class="{ 'box-card--active': isActive }"
    hover-class="none"
  >
    <view class="card-content">
      <!-- 图标区域 -->
      <view class="icon-box">
        <view class="icon-frame"></view>
        <u-icon
          :name="getIcon(box_type)"
          size="48"
          color="#3B82F6"
          class="icon-main"
        />
      </view>

      <!-- 文字信息 -->
      <view class="info">
        <text class="name">{{ box_name }}</text>
        <text class="desc">{{ box_code }} · {{ getBoxTypeText(idx_user_box_type) }}</text>
        <text class="time">最后心跳：{{ formatDate(last_heartbeat_time) }}</text>
      </view>

      <!-- 电池状态 -->
      <view 
        v-if="battery !== null && battery >= 0" 
        class="battery-badge" 
        :class="{ 'low-battery': battery < 20 }"
      >
        <u-icon
          name="battery"
          size="18"
          :color="battery < 20 ? '#EF4444' : '#3B82F6'"
          class="battery-icon"
        />
        <text class="battery-text">{{ battery }}%</text>
      </view>

      <!-- 右侧箭头 -->
      <view class="arrow-wrapper">
        <view class="arrow-line" :class="{ 'arrow-line--active': isActive }"></view>
        <u-icon 
          name="arrow-right" 
          color="#3B82F6" 
          size="24"
          class="arrow-icon"
          :class="{ 'arrow-icon--active': isActive }"
        />
      </view>
    </view>
  </u-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
  id: { type: [String, Number], default: 0 },
  box_code: { type: String, default: '' },
  user_id: { type: [String, Number], default: 0 },
  box_name: { type: String, default: '收纳盒' },
  box_type: { type: [String, Number], default: 0 },
  idx_user_box_type: { type: [String, Number], default: 0 },
  status: { type: [String, Number], default: 1 },
  rssi: { type: Number, default: null },
  battery: { type: Number, default: null },
  last_heartbeat_time: [String, Date],
  create_time: [String, Date],
  update_time: [String, Date],
  border: { type: Boolean, default: false },
  margin: { type: [String, Number], default: '24rpx' },
  padding: { type: [String, Number], default: '32rpx' }
})

// 调试信息
onMounted(() => {
  console.log('📦 boxcard 接收到的数据:', {
    id: props.id,
    box_code: props.box_code,
    box_name: props.box_name,
    box_type: props.box_type,
    idx_user_box_type: props.idx_user_box_type,
    battery: props.battery,
    last_heartbeat_time: props.last_heartbeat_time
  })
})

const emit = defineEmits(['click'])
const isActive = ref(false)

// 根据 box_type 获取图标
const getIcon = (boxType) => {
  const icons = {
    0: 'inbox',       // 默认收纳盒
    1: 'layers',      // 抽屉
    2: 'book',        // 书架
    3: 'tshirt',      // 衣柜
    4: 'ice-cream',   // 冰箱
    5: 'home',        // 橱柜
    default: 'cube'
  }
  return icons[boxType] || icons.default
}

// 获取盒子类型文本
const getBoxTypeText = (boxType) => {
  const typeMap = {
    0: '子盒',
    1: '主盒'
  }
  return typeMap[boxType] || '未知类型'
}

// 日期格式化
const formatDate = (date) => {
  if (!date) return '暂无记录'
  try {
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  } catch (e) {
    return '日期错误'
  }
}

// 点击处理
const handleClick = () => {
  if (uni.vibrateShort) uni.vibrateShort({ type: 'light' })
  isActive.value = true
  emit('click')
  setTimeout(() => {
    isActive.value = false
  }, 120)
}
</script>

<style scoped>
/* 样式保持不变 */
.box-card {
  background-color: #fff;
  border-radius: 20rpx;
  border: 1rpx solid #f0f2f5;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.02);
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
}

.box-card--active {
  border-color: #93c5fd;
  box-shadow: 0 8rpx 32rpx rgba(59, 130, 246, 0.12);
  background: linear-gradient(180deg, rgba(249, 250, 251, 1) 0%, rgba(240, 249, 255, 1) 100%);
}

.box-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 6rpx;
  height: 100%;
  background: linear-gradient(180deg, #3B82F6 0%, #818CF8 100%);
  opacity: 0;
  transition: opacity 0.25s ease;
}
.box-card--active::before {
  opacity: 1;
}

.card-content {
  display: flex;
  align-items: center;
  position: relative;
  min-height: 160rpx;
  padding: 0 20rpx;
}

.icon-box {
  width: 100rpx;
  height: 100rpx;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: 20rpx;
}

.icon-frame {
  position: absolute;
  width: 80rpx;
  height: 80rpx;
  border: 2rpx solid #dbeafe;
  border-radius: 16rpx;
  transform: rotate(45deg);
  transition: all 0.3s ease;
}
.box-card--active .icon-frame {
  background-color: rgba(239, 246, 255, 0.6);
  transform: rotate(45deg) scale(1.15);
  border-color: #bfdbfe;
}

.icon-main {
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}
.box-card--active .icon-main {
  transform: scale(1.1);
}

.info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.name {
  font-size: 32rpx;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8rpx;
}
.box-card--active .name {
  color: #2563eb;
}

.desc {
  font-size: 26rpx;
  color: #64748b;
  line-height: 1.4;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4rpx;
}

.time {
  font-size: 22rpx;
  color: #94a3b8;
  display: block;
  line-height: 1.4;
}

.battery-badge {
  position: absolute;
  top: 20rpx;
  right: 80rpx;
  display: flex;
  align-items: center;
  background-color: #f8fafc;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
  color: #3B82F6;
  border: 1rpx solid #dbeafe;
  z-index: 2;
}

.low-battery {
  background-color: #fef2f2;
  border-color: #fee2e2;
  color: #EF4444;
}

.arrow-wrapper {
  display: flex;
  align-items: center;
  margin-left: auto;
  flex-shrink: 0;
}

.arrow-line {
  width: 20rpx;
  height: 2rpx;
  background-color: #dbeafe;
  transition: all 0.3s ease;
  margin-right: 8rpx;
}
.arrow-line--active {
  background-color: #60a5fa;
  width: 30rpx;
}

.arrow-icon {
  transition: transform 0.3s ease;
}
.arrow-icon--active {
  transform: translateX(4rpx);
}

@media (max-width: 375px) {
  .card-content {
    padding: 0 16rpx;
  }
  
  .icon-box {
    width: 80rpx;
    height: 80rpx;
    margin-right: 16rpx;
  }
  
  .icon-frame {
    width: 60rpx;
    height: 60rpx;
  }
  
  .name {
    font-size: 28rpx;
  }
  
  .desc {
    font-size: 24rpx;
  }
  
  .time {
    font-size: 20rpx;
  }
  
  .battery-badge {
    right: 70rpx;
    top: 16rpx;
  }
}
</style>