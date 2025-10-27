<template>
  <view class="container">
    <u-navbar :title="itemName" title-bold title-color="#333" />

    <view class="content">
      <view class="item-detail">
        <!-- 图片区域 -->
        <view class="image-section">
          <u-image
            v-if="itemData.image"
            :src="itemData.image"
            width="300rpx"
            height="300rpx"
            radius="16rpx"
            mode="aspectFit"
          />
          <view v-else class="image-placeholder">
            <u-icon :name="getIcon(itemData.category)" size="80" color="#ccc" />
            <text class="placeholder-text">暂无图片</text>
          </view>
        </view>

        <!-- 基本信息 -->
        <view class="info-section">
          <view class="info-item">
            <text class="label">物品名称</text>
            <text class="value">{{ itemData.name }}</text>
          </view>

          <view class="info-item">
            <text class="label">物品分类</text>
            <text class="value">{{ itemData.category }}</text>
          </view>

          <view class="info-item">
            <text class="label">物品描述</text>
            <text class="value">{{ itemData.description || "暂无描述" }}</text>
          </view>

          <view class="info-item">
            <text class="label">数量</text>
            <text class="value">{{ itemData.quantity }} 件</text>
          </view>

          <view class="info-item">
            <text class="label">所在位置</text>
            <text class="value">{{ itemData.location }}</text>
          </view>

          <view class="info-item">
            <text class="label">最后更新</text>
            <text class="value">{{ formatDate(itemData.lastModified) }}</text>
          </view>
        </view>

        <!-- 操作按钮 -->
        <view class="action-buttons">
          <u-button type="primary" @click="editItem" icon="edit-pen">
            编辑物品
          </u-button>
          <u-button
            type="error"
            @click="deleteItem"
            icon="trash"
            style="margin-top: 20rpx"
          >
            删除物品
          </u-button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { onLoad } from "@dcloudio/uni-app";

const itemId = ref("");
const itemName = ref("物品详情");
const itemData = ref({
  name: "",
  category: "",
  description: "",
  quantity: 1,
  location: "",
  lastModified: "",
  image: "",
});

onLoad((options) => {
  itemId.value = options.id || "";
  loadItemData();
});

const loadItemData = async () => {
  // 模拟加载数据
  await new Promise((resolve) => setTimeout(resolve, 500));

  // 模拟数据
  itemData.value = {
    id: itemId.value,
    name: "螺丝刀套装",
    category: "工具",
    description: "多功能家用螺丝刀，包含多种规格螺丝刀头",
    quantity: 1,
    location: "工具箱",
    lastModified: "2024-01-15",
    image: "",
  };

  itemName.value = itemData.value.name;
};

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
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`;
};

const editItem = () => {
  uni.showToast({
    title: "编辑功能开发中",
    icon: "none",
  });
};

const deleteItem = () => {
  uni.showModal({
    title: "确认删除",
    content: "确定要删除这个物品吗？此操作不可恢复。",
    confirmColor: "#ff4444",
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({ title: "删除中..." });

        setTimeout(() => {
          uni.hideLoading();
          uni.showToast({
            title: "删除成功",
            icon: "success",
          });

          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        }, 1000);
      }
    },
  });
};
</script>

<style scoped>
.container {
  background-color: #f8f9fa;
  min-height: 100vh;
}

.content {
  padding: 40rpx;
}

.item-detail {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 40rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.03);
}

.image-section {
  display: flex;
  justify-content: center;
  margin-bottom: 40rpx;
}

.image-placeholder {
  width: 300rpx;
  height: 300rpx;
  background-color: #f8f8f8;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.placeholder-text {
  font-size: 24rpx;
  margin-top: 20rpx;
}

.info-section {
  margin-bottom: 40rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #666;
  font-weight: 500;
  min-width: 150rpx;
}

.value {
  font-size: 28rpx;
  color: #333;
  text-align: right;
  flex: 1;
  margin-left: 20rpx;
}

.action-buttons {
  margin-top: 40rpx;
}
</style>
