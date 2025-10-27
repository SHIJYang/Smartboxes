<template>
  <view class="search-container">
    <u-navbar :title="$t('search.title')" />

    <!-- 搜索框 -->
    <view class="search-box">
      <u-search
        v-model="searchText"
        :placeholder="$t('search.placeholder')"
        @search="handleSearch"
        @clear="handleClear"
      />
    </view>

    <!-- 分类筛选 -->
    <view class="category-filter">
      <u-tabs
        :list="
          categories.map((c) => ({ name: $t(`search.category.${c.key}`) }))
        "
        @click="onCategoryChange"
      />
    </view>

    <!-- 搜索结果 -->
    <scroll-view scroll-y class="result-list" @scrolltolower="loadMore">
      <u-empty
        v-if="items.length === 0"
        :text="$t('search.empty')"
        mode="search"
      />
      <view class="item-list">
        <view
          class="item-card"
          v-for="item in items"
          :key="item.id"
          @click="viewDetail(item)"
        >
          <u-image
            :src="item.image"
            width="180rpx"
            height="180rpx"
            radius="12"
            :fade="true"
          />
          <view class="item-info">
            <text class="item-name">{{ item.itemName }}</text>
            <text class="item-desc">{{ item.description }}</text>
            <view class="item-location">
              <u-icon name="map" size="28rpx" color="#666" />
              <text>{{ item.boxName }}</text>
            </view>
          </view>
        </view>
      </view>
      <u-loadmore v-if="hasMore" status="loading" />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import http from "@/utils/request";
import { platform } from "@/utils/platform";

const searchText = ref("");
const categories = ref([
  { key: "all" },
  { key: "book" },
  { key: "electronic" },
  { key: "clothing" },
  { key: "stationery" },
]);
const items = ref([]);
const page = ref(1);
const hasMore = ref(true);

const handleSearch = () => {
  // TODO: 调用搜索API
};

const handleClear = () => {
  items.value = [];
};

const onCategoryChange = (index) => {
  // TODO: 按分类筛选
};

const viewDetail = (item) => {
  uni.navigateTo({
    url: `/pages/item/detail?id=${item.id}`,
  });
};

const loadData = async () => {
  try {
    const data = await http.get("/items", {
      page: page.value,
      category: currentCategory.value,
    });
    items.value.push(...data);
    hasMore.value = data.length === 10;
  } catch (e) {
    uni.showToast({ title: "加载失败", icon: "error" });
  }
};

const loadMore = () => {
  if (!hasMore.value) return;
  page.value++;
  loadData();
};

// 扫码功能（仅App和小程序支持）
const scanCode = async () => {
  if (platform.isH5) return;
  try {
    const res = await uni.scanCode();
    if (res.result) {
      searchText.value = res.result;
      handleSearch();
    }
  } catch (e) {
    uni.showToast({ title: "扫码失败", icon: "error" });
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.search-container {
  min-height: 100vh;
  background: #f5f5f5;
}
.search-box {
  padding: 20rpx;
  background: #fff;
}
.category-filter {
  margin: 20rpx 0;
  background: #fff;
}
.result-list {
  height: calc(100vh - 300rpx);
}
.item-list {
  padding: 20rpx;
}
.item-card {
  margin-bottom: 30rpx;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}
.item-card:active {
  transform: scale(0.98);
}
.item-name {
  font-size: 28rpx;
  font-weight: bold;
}
.item-desc {
  font-size: 24rpx;
  color: #999;
  margin: 8rpx 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
.item-location {
  font-size: 24rpx;
  color: #666;
  margin-top: 10rpx;
}
</style>
