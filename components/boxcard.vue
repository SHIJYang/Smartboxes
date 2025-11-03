<template>
  <!-- 统一uview-plus组件使用，补充hover反馈配置 -->
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
      <!-- 1. 图标区域：简化设计，确保兼容性 -->
      <view class="icon-box">
        <view class="icon-frame"></view>
        <u-icon
          :name="getIcon(category)"
          size="48"
          color="#3B82F6"
          class="icon-main"
        />
      </view>

      <!-- 2. 文字信息：优化布局 -->
      <view class="info">
        <text class="name">{{ name }}</text>
        <text class="desc">{{ itemCount }} 件物品 · {{ location || '未设置位置' }}</text>
        <text class="time">最后整理：{{ formatDate(lastModified) }}</text>
      </view>

      <!-- 3. 电池状态：调整定位 -->
      <view 
        v-if="showBattery" 
        class="battery-badge" 
        :class="[
          { 'low-battery': isLowBattery },  
          { 'battery-badge--charging': isCharging }
        ]"
      >
        <u-icon
          :name="isCharging ? 'battery-charging' : 'battery'"
          size="18"
          :color="isLowBattery && !isCharging ? '#EF4444' : '#3B82F6'"
          class="battery-icon"
        />
        <text class="battery-text">{{ batteryLevel }}%</text>
      </view>

      <!-- 4. 右侧箭头 -->
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
import { ref, computed,onMounted } from 'vue'

// 保持原有props/emit逻辑，补充默认值防错
const props = defineProps({
  name: { type: String, default: '未命名收纳' },
  category: { type: String, default: 'box' },
  itemCount: { type: Number, default: 0 },
  location: { type: String, default: '' },
  lastModified: [String, Date],
  batteryLevel: { type: Number, default: 0 },
  isCharging: { type: Boolean, default: false },
  border: { type: Boolean, default: false },
  margin: { type: [String, Number], default: '24rpx' },
  padding: { type: [String, Number], default: '32rpx' }
})

// 调试：打印接收到的props
onMounted(() => {
  console.log('📦 boxcard 接收到的数据:', {
    name: props.name,
    category: props.category,
    itemCount: props.itemCount,
    location: props.location,
    lastModified: props.lastModified,
    batteryLevel: props.batteryLevel,
    isCharging: props.isCharging
  })
})

const emit = defineEmits(['click'])

// 保持原有计算属性逻辑
const showBattery = computed(() => props.batteryLevel !== null && props.batteryLevel >= 0)
const isLowBattery = computed(() => {
  return showBattery.value && !props.isCharging && props.batteryLevel < 20
})

const isActive = ref(false)

// 图标映射：补充更多分类图标，适配不同收纳场景
const getIcon = (cat) => {
  const icons = {
    box: 'inbox',       // 收纳盒
    drawer: 'layers',   // 抽屉
    shelf: 'book',      // 书架
    wardrobe: 'tshirt', // 衣柜
    fridge: 'ice-cream',// 冰箱
    cabinet: 'home',    // 橱柜
    default: 'cube'     // 默认
  }
  return icons[cat] || icons.default
}

// 日期格式化：保持原有逻辑，优化空值显示
const formatDate = (date) => {
  if (!date) return '暂无记录'
  try {
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  } catch (e) {
    return '日期错误'
  }
}

// 点击逻辑：保持原有震动+状态切换，优化延迟时间
const handleClick = () => {
  // 兼容不同环境的震动API
  if (uni.vibrateShort) uni.vibrateShort({ type: 'light' })
  isActive.value = true
  emit('click')
  // 缩短延迟，提升反馈灵敏度
  setTimeout(() => {
    isActive.value = false
  }, 120)
}
</script>

<style scoped>
/* 基础卡片样式：简化样式确保兼容性 */
.box-card {
  background-color: #fff;
  border-radius: 20rpx;
  border: 1rpx solid #f0f2f5;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.02);
  transition: all 0.25s ease;
  position: relative;
  overflow: hidden;
}

/* 激活态样式 */
.box-card--active {
  border-color: #93c5fd;
  box-shadow: 0 8rpx 32rpx rgba(59, 130, 246, 0.12);
  background: linear-gradient(180deg, rgba(249, 250, 251, 1) 0%, rgba(240, 249, 255, 1) 100%);
}

/* 左侧激活光条 */
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

/* 卡片内容容器：使用更兼容的布局 */
.card-content {
  display: flex;
  align-items: center;
  position: relative;
  min-height: 160rpx;
  padding: 0 20rpx;
}

/* 图标区域：简化设计 */
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

/* 框架 */
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

/* 主图标 */
.icon-main {
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}
.box-card--active .icon-main {
  transform: scale(1.1);
}

/* 文字信息：使用弹性布局 */
.info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* 名称 */
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

/* 描述 */
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

/* 时间 */
.time {
  font-size: 22rpx;
  color: #94a3b8;
  display: block;
  line-height: 1.4;
}

/* 电池状态：调整定位方式 */
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

/* 低电量样式 */
.low-battery {
  background-color: #fef2f2;
  border-color: #fee2e2;
  color: #EF4444;
}

/* 充电状态动画 */
.battery-badge--charging .battery-icon {
  animation: chargePulse 1.2s infinite alternate;
}

/* 右侧箭头 */
.arrow-wrapper {
  display: flex;
  align-items: center;
  margin-left: auto;
  flex-shrink: 0;
}

/* 箭头前的线条 */
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

/* 箭头图标 */
.arrow-icon {
  transition: transform 0.3s ease;
}
.arrow-icon--active {
  transform: translateX(4rpx);
}

/* 充电闪烁动画 */
@keyframes chargePulse {
  0% { opacity: 0.7; }
  100% { opacity: 1; }
}

/* 响应式调整 */
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