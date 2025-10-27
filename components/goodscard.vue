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
          <u-icon :name="getIcon(category)" size="40" color="#888" />
        </view>
      </view>

      <!-- 物品信息 -->
      <view class="info">
        <text class="name">{{ name }}</text>
        <text class="desc">{{ description }}</text>
        <view class="meta-info">
          <text class="quantity">数量: {{ quantity }}</text>
          <text class="location">位置: {{ location }}</text>
          <text class="time">最后更新: {{ formatDate(lastModified) }}</text>
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
import { ref } from "vue";

const props = defineProps({
  name: String,
  description: String,
  category: String,
  image: String,
  quantity: { type: Number, default: 1 },
  location: String,
  lastModified: [String, Date],
  border: { type: Boolean, default: true },
  margin: { type: [String, Number], default: "20rpx" },
  padding: { type: [String, Number], default: "24rpx" },
});

const emit = defineEmits(["click"]);

const getIcon = (cat) => {
  const icons = {
    pen: "edit",
    key: "key",
    book: "book",
    clothes: "tshirt",
    tool: "wrench",
    工具: "wrench",
    文具: "edit",
    衣物: "tshirt",
    书籍: "book",
    电子: "mobile",
    default: "cube",
  };
  return icons[cat] || icons.default;
};

const formatDate = (date) => {
  if (!date) return "暂无记录";
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(
    2,
    "0"
  )}-${String(d.getDate()).padStart(2, "0")}`;
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

.quantity,
.location,
.time {
  font-size: 22rpx;
  color: #94a3b8;
  display: block;
  line-height: 1.4;
}

.arrow-wrapper {
  margin-left: auto;
  padding-left: 20rpx;
}
</style>
