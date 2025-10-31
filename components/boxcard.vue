<template>
  <!-- 统一uview-plus组件使用，补充hover反馈配置 -->
  <u-card
    :border="false"
    :margin="margin"
    :padding="padding"
    @click="handleClick"
    class="box-card"
    :class="{ 'box-card--active': isActive, 'box-card--hover': !isActive }"
    hover-class="none"
  >
    <view class="card-content">
      <!-- 1. 图标区域：新增渐变光效，强化科技感 -->
      <view class="icon-box">
        <view class="icon-frame"></view>
        
        
        <u-icon
          :name="getIcon(category)"
          size="48"
          color="#3B82F6"
          class="icon-main"
        />
      </view>

      <!-- 2. 文字信息：优化字号层级，新增文字过渡 -->
      <view class="info">
        <text class="name">{{ name }}</text>
        <text class="desc">{{ itemCount }} 件物品 · {{ location || '未设置位置' }}</text>
        <text class="time">最后整理：{{ formatDate(lastModified) }}</text>
      </view>

      <!-- 3. 电池状态：优化布局，新增充电动画 -->
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

      <!-- 4. 右侧箭头：优化动画逻辑，新增路径过渡 -->
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
import { ref, computed } from 'vue'

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
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
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
/* 基础卡片样式：优化阴影层级，新增过渡时长 */
.box-card {
  background-color: #fff;
  border-radius: 20rpx; /* 增大圆角，更柔和 */
  border: 1rpx solid #f0f2f5; /* 浅色边框，减少突兀感 */
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.02); /* 更淡的阴影，提升通透感 */
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1); /* 缓动曲线优化，过渡更自然 */
  position: relative;
  overflow: hidden;
}

/* 1. 非激活态hover反馈：新增轻微上浮+阴影变化 */
.box-card--hover {
  transition: all 0.3s ease;
}
.box-card--hover:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 6rpx 24rpx rgba(0, 0, 0, 0.04);
}

/* 2. 激活态样式：强化边框+左侧光条，新增背景渐变 */
.box-card--active {
  border-color: #93c5fd;
  box-shadow: 0 8rpx 32rpx rgba(59, 130, 246, 0.12);
  /* 新增背景渐变，提升层次感 */
  background: linear-gradient(180deg, rgba(249, 250, 251, 1) 0%, rgba(240, 249, 255, 1) 100%);
}

/* 左侧激活光条：优化宽度和过渡 */
.box-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 6rpx; /* 加宽光条，更醒目 */
  height: 100%;
  background: linear-gradient(180deg, #3B82F6 0%, #818CF8 100%); /* 渐变光条，更有质感 */
  opacity: 0;
  transition: opacity 0.25s ease;
}
.box-card--active::before {
  opacity: 1;
}

/* 卡片内容容器：优化间距，确保对齐 */
.card-content {
  display: flex;
  align-items: center;
  gap: 36rpx; /* 增大间距，减少拥挤感 */
  position: relative;
  min-height: 160rpx; /* 增高最小高度，提升容纳性 */
}

/* 3. 图标区域：新增光晕+旋转动画，强化科技感 */
.icon-box {
  width: 100rpx;
  height: 100rpx;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 框架：优化颜色和旋转过渡 */
.icon-frame {
  position: absolute;
  width: 80rpx;
  height: 80rpx;
  border: 2rpx solid #dbeafe;
  border-radius: 16rpx; /* 增大圆角，更协调 */
  transform: rotate(45deg);
  transition: all 0.3s ease;
}
.box-card--active .icon-frame {
  background-color: rgba(239, 246, 255, 0.6); /* 半透明背景，更通透 */
  transform: rotate(45deg) scale(1.15); /* 增大缩放，更有张力 */
  border-color: #bfdbfe;
}

/* 新增光晕：激活时显示，模拟发光效果 */
.icon-glow {
  position: absolute;
  width: 90rpx;
  height: 90rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, rgba(59, 130, 246, 0) 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}
.icon-glow--active {
  opacity: 1;
  animation: glowPulse 1.5s infinite ease-in-out; /* 呼吸动画，更灵动 */
}

/* 主图标：新增轻微缩放过渡 */
.icon-main {
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}
.box-card--active .icon-main {
  transform: scale(1.1);
}

/* 4. 文字信息：优化字号和颜色，新增过渡 */
.info {
  flex: 1;
  min-width: 0;
}

/* 名称：加粗优化，调整字号 */
.name {
  font-size: 36rpx;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s ease;
}
.box-card--active .name {
  color: #2563eb; /* 激活时文字变色，强化关联 */
}

/* 描述：调整颜色，更柔和 */
.desc {
  font-size: 28rpx;
  color: #64748b;
  margin: 8rpx 0;
  line-height: 1.5;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 时间：调整字号和颜色，更低调 */
.time {
  font-size: 24rpx;
  color: #94a3b8;
  display: block;
  margin-top: 4rpx;
}

/* 5. 电池状态：优化布局和动画 */
.battery-badge {
  position: absolute;
  top: 28rpx;
  right: 90rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  background-color: #f8fafc;
  padding: 6rpx 16rpx;
  border-radius: 8rpx; /* 增大圆角 */
  font-size: 22rpx;
  color: #3B82F6;
  border: 1rpx solid #dbeafe;
  transition: all 0.3s ease;
}

/* 低电量样式：强化警示色 */
.low-battery {
  background-color: #fef2f2;
  border-color: #fee2e2;
  color: #EF4444;
}

/* 充电状态：新增闪烁动画 */
.battery-badge--charging .battery-icon {
  animation: chargePulse 1.2s infinite alternate;
}

/* 6. 右侧箭头：优化动画，新增路径过渡 */
.arrow-wrapper {
  display: flex;
  align-items: center;
  gap: 16rpx; /* 增大间距 */
  margin-left: auto;
  padding-left: 20rpx;
  position: relative;
}

/* 箭头前的线条：优化宽度和过渡 */
.arrow-line {
  width: 30rpx;
  height: 1rpx;
  background-color: #dbeafe;
  transition: all 0.3s ease;
}
.arrow-line--active {
  background-color: #60a5fa;
  width: 45rpx; /* 更长的延伸 */
}

/* 箭头图标：新增平移动画 */
.arrow-icon {
  transition: transform 0.3s ease;
}
.arrow-icon--active {
  transform: translateX(6rpx); /* 激活时右移，模拟"准备跳转" */
}

/* 7. 新增关键帧动画：提升交互质感 */
/* 光晕呼吸动画 */
@keyframes glowPulse {
  0% { opacity: 0.7; transform: scale(1); }
  100% { opacity: 1; transform: scale(1.1); }
}

/* 充电闪烁动画 */
@keyframes chargePulse {
  0% { opacity: 0.7; }
  100% { opacity: 1; color: #2563eb; }
}
</style>