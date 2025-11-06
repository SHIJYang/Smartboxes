<template>
  <u-card
    :border="border"
    :margin="margin"
    :padding="padding"
    class="item-card"
    @click="handleClick"
  >
    <view class="card-content">
      <!-- 图片或占位图标 -->
      <view class="image-box">
        <u-image
          v-if="image"
          :src="image"
          width="140rpx"
          height="140rpx"
          radius="12rpx"
          lazy-load
        />
        <view v-else class="placeholder-icon">
          <u-icon :name="getIcon(item_tag)" size="40" color="#888" />
        </view>
      </view>

      <!-- 物品信息 -->
      <view class="info">
        <text class="name">{{ displayName }}</text>
        <text class="desc">{{ item_desc || '暂无描述' }}</text>
        <view class="meta-info">
          <text class="box">物品编码: {{ item_code }}</text>
          <text class="time">放入时间: {{ formatDate(put_in_time) }}</text>
          <text class="expire" v-if="expire_time">到期时间: {{ formatDate(expire_time) }}</text>
          <text class="status">状态: {{ isValid ? '有效' : '无效' }}</text>
        </view>
      </view>

      <!-- 右侧箭头 -->
      <view class="arrow-wrapper">
        <u-icon name="arrow-right" color="#3B82F6" size="24" />
      </view>
    </view>
  </u-card>
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  id: [String, Number],
  item_code: String,
  box_id: [String, Number],
  auto_recognize_name: String,
  manual_edit_name: String,
  item_tag: String,
  item_desc: String,
  put_in_time: [String, Date],
  expire_time: [String, Date],
  is_valid: { type: [String, Number, Boolean], default: 1 },
  create_time: [String, Date],
  update_time: [String, Date],
  image: String,
  border: { type: Boolean, default: true },
  margin: { type: [String, Number], default: "20rpx" },
  padding: { type: [String, Number], default: "24rpx" },
});

const emit = defineEmits(["click"]);

// 计算显示名称：优先显示手动编辑名称
const displayName = computed(() => {
  return props.manual_edit_name || props.auto_recognize_name || '未命名物品'
})

// 计算有效状态
const isValid = computed(() => {
  return props.is_valid === 1 || props.is_valid === true
})

const getIcon = (tag) => {
  const icons = {
    '办公用品': "edit",
    '日常用品': "cube",
    '文具': "edit-pen",
    '工具': "wrench",
    '衣物': "tshirt",
    '书籍': "book",
    '电子': "mobile",
    '默认': "cube",
  };
  return icons[tag] || icons['默认'];
};

const formatDate = (date) => {
  if (!date) return "暂无记录";
  try {
    const d = new Date(date);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  } catch (e) {
    return "日期错误";
  }
};

const handleClick = () => {
  emit("click");
};
</script>

<style scoped>
.item-card {
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.03);
  transition: all 0.2s ease;
}

.item-card:active {
  background-color: #f8fafc;
  transform: scale(0.98);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 24rpx;
  position: relative;
}

.image-box {
  width: 140rpx;
  height: 140rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f8f8;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.placeholder-icon {
  color: #aaa;
}

.info {
  flex: 1;
  min-width: 0;
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

.meta-info {
  margin-top: 8rpx;
}

.box,
.time,
.expire,
.status {
  font-size: 22rpx;
  color: #94a3b8;
  display: block;
  line-height: 1.4;
}

.expire {
  color: #f59e0b;
}

.status {
  color: #10b981;
}

.arrow-wrapper {
  margin-left: auto;
  padding-left: 20rpx;
}
</style>