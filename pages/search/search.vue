<template>
  <view class="search-container" :class="{ 'platform-container': true }">
    <u-navbar title="物品搜索" />

    <!-- 搜索框 -->
    <view class="search-box">
      <!-- #ifdef MP-WEIXIN -->
      <u-search
        v-model="searchText"
        placeholder="搜索物品名称"
        show-action
        @search="handleSearch"
        @clear="handleClear"
      />
      <!-- #endif -->

      <!-- #ifndef MP-WEIXIN -->
      <u-search
        v-model="searchText"
        placeholder="搜索物品名称"
        @search="handleSearch"
        @clear="handleClear"
      >
        <template #suffix>
          <u-icon name="scan" @click="scanCode" />
        </template>
      </u-search>
      <!-- #endif -->
    </view>

    <!-- 分类筛选 -->
    <view class="category-filter">
      <u-tabs
        :list="categories.map((name) => ({ name }))"
        @click="onCategoryChange"
      />
    </view>

    <!-- 搜索结果 -->
    <scroll-view scroll-y class="result-list" @scrolltolower="loadMore">
      <u-empty v-if="items.length === 0 && !loading" text="暂无物品" mode="search" />
      <u-loading v-else-if="loading" mode="circle" size="40" color="#4A90E2" />
      <view class="item-list" v-else>
        <goodscard
          v-for="item in items"
          :key="item.id"
          :id="item.id"
          :item_code="item.item_code"
          :box_id="item.box_id"
          :auto_recognize_name="item.auto_recognize_name"
          :manual_edit_name="item.manual_edit_name"
          :item_tag="item.item_tag"
          :item_desc="item.item_desc"
          :put_in_time="item.put_in_time"
          :expire_time="item.expire_time"
          :is_valid="item.is_valid"
          @click="viewDetail(item)"
        />
      </view>
      <u-loadmore v-if="hasMore && items.length > 0" status="loading" />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getItems } from "@/api/items/itemsapi.js";
import goodscard from "@/components/goodscard.vue";

const searchText = ref("");
const categories = ref(["全部", "办公用品", "日常用品", "工具", "文具", "衣物", "书籍", "电子"]);
const currentCategory = ref("");
const items = ref([]);
const page = ref(1);
const hasMore = ref(true);
const loading = ref(false);

const handleSearch = () => {
  page.value = 1;
  items.value = [];
  loadData();
};

const handleClear = () => {
  searchText.value = "";
  items.value = [];
};

const onCategoryChange = (index) => {
  currentCategory.value = categories.value[index] === "全部" ? "" : categories.value[index];
  page.value = 1;
  items.value = [];
  loadData();
};

const viewDetail = (item) => {
  uni.navigateTo({
    url: `/pages/item/item?id=${item.id}`,
  });
};

const loadData = async () => {
  if (loading.value) return;
  
  loading.value = true;
  try {
    const params = {
      page: page.value,
      item_tag: currentCategory.value,
      keyword: searchText.value,
    };
    const res = await getItems(params);
    if (res && res.code === 200) {
      if (page.value === 1) {
        items.value = res.data || [];
      } else {
        items.value.push(...(res.data || []));
      }
      hasMore.value = (res.data || []).length >= 10;
    } else {
      uni.showToast({ title: "加载失败", icon: "none" });
    }
  } catch (e) {
    console.error("搜索失败:", e);
    uni.showToast({ title: "加载失败", icon: "none" });
  } finally {
    loading.value = false;
  }
};

const loadMore = () => {
  if (!hasMore.value || loading.value) return;
  page.value++;
  loadData();
};

// 扫码功能
const scanCode = async () => {
  try {
    const res = await uni.scanCode();
    if (res.result) {
      searchText.value = res.result;
      handleSearch();
    }
  } catch (e) {
    uni.showToast({ title: "扫码失败", icon: "none" });
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
  padding: 20rpx;
}
.item-list {
  padding-bottom: 20rpx;
}
</style>