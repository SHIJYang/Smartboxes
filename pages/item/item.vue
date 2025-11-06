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
            <u-icon :name="getIcon(itemData.item_tag)" size="80" color="#ccc" />
            <text class="placeholder-text">暂无图片</text>
          </view>
        </view>

        <!-- 基本信息 -->
        <view class="info-section">
          <view class="info-item">
            <text class="label">物品编码</text>
            <text class="value">{{ itemData.item_code }}</text>
          </view>

          <view class="info-item">
            <text class="label">物品名称</text>
            <text class="value">{{ displayName }}</text>
          </view>

          <view class="info-item">
            <text class="label">物品分类</text>
            <text class="value">{{ itemData.item_tag || '未分类' }}</text>
          </view>

          <view class="info-item">
            <text class="label">物品描述</text>
            <text class="value">{{ itemData.item_desc || "暂无描述" }}</text>
          </view>

          <view class="info-item">
            <text class="label">收纳盒</text>
            <text class="value">{{ itemData.box_id }}号盒</text>
          </view>

          <view class="info-item">
            <text class="label">放入时间</text>
            <text class="value">{{ formatDate(itemData.put_in_time) }}</text>
          </view>

          <view class="info-item" v-if="itemData.expire_time">
            <text class="label">到期时间</text>
            <text class="value expire">{{ formatDate(itemData.expire_time) }}</text>
          </view>

          <view class="info-item">
            <text class="label">物品状态</text>
            <text class="value" :class="{ 'status-valid': itemData.is_valid, 'status-invalid': !itemData.is_valid }">
              {{ itemData.is_valid ? '有效' : '无效' }}
            </text>
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
import { ref, computed, onMounted } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { getItemDetail } from "@/api/items/itemsapi.js"; 

const itemId = ref("");
const itemName = ref("物品详情");
const itemData = ref({
  id: "",
  item_code: "",
  box_id: "",
  auto_recognize_name: "",
  manual_edit_name: "",
  item_tag: "",
  item_desc: "",
  put_in_time: "",
  expire_time: "",
  is_valid: 1,
  create_time: "",
  update_time: "",
});

onLoad((options) => {
  itemId.value = options.id || "";
  loadItemData();
});

// 计算显示名称
const displayName = computed(() => {
  return itemData.value.manual_edit_name || itemData.value.auto_recognize_name || '未命名物品';
});

const loadItemData = async () => {
  try {
    const res = await getItemDetail(itemId.value);
    if (res && res.code === 200) {
      itemData.value = res.data;
      itemName.value = displayName.value;
    } else {
      // 加载失败时显示默认数据
      itemName.value = "物品详情";
    }
  } catch (error) {
    console.error("加载物品详情失败:", error);
    uni.showToast({
      title: "加载失败",
      icon: "none",
    });
  }
};

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
    return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`;
  } catch (e) {
    return "日期错误";
  }
};

const editItem = () => {
  uni.navigateTo({
    url: `/pages/edit-item/edit-item?id=${itemId.value}`
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
        // 调用删除API
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

.expire {
  color: #f59e0b;
}

.status-valid {
  color: #10b981;
}

.status-invalid {
  color: #ef4444;
}

.action-buttons {
  margin-top: 40rpx;
}
</style>