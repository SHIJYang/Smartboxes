<!-- components/物品卡片.vue -->
<template>
  <u-card
    :border="border"
    :margin="margin"
    :padding="padding"
    class="item-card"
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
        <view class="tags">
          <u-tag
            v-for="tag in tags"
            :key="tag"
            :text="tag"
            type="info"
            size="mini"
            margin-right="10rpx"
          />
        </view>
      </view>

      <!-- 操作区 -->
      <view class="actions">
        <u-button
          size="mini"
          type="primary"
          text="编辑"
          @click="emit('edit')"
        />
        <u-button
          size="mini"
          type="error"
          text="删除"
          @click="emit('delete')"
          style="margin-top: 12rpx;"
        />
      </view>
    </view>
  </u-card>
</template>

<script setup>
const props = defineProps({
  name: String,
  description: String,
  category: String,
  image: String,
  tags: { type: Array, default: () => [] },
  border: { type: Boolean, default: true },
  margin: { type: [String, Number], default: '20rpx' },
  padding: { type: [String, Number], default: '24rpx' }
})

const emit = defineEmits(['edit', 'delete'])

const getIcon = (cat) => {
  const icons = {
    pen: 'edit',
    key: 'key',
    book: 'book',
    clothes: 'tshirt',
    tool: 'wrench',
    default: 'cube'
  }
  return icons[cat] || icons.default
}
</script>

<style scoped>
.item-card {
  background-color: #fff;
}
.card-content {
  display: flex;
  gap: 24rpx;
}
.image-box {
  width: 140rpx;
  height: 140rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f8f8;
  border-radius: 12rpx;
}
.placeholder-icon {
  color: #aaa;
}
.name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}
.desc {
  font-size: 24rpx;
  color: #666;
  margin: 8rpx 0;
}
.tags {
  margin-top: 12rpx;
  display: flex;
  flex-wrap: wrap;
}
.actions {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  