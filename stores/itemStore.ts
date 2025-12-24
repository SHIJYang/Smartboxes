// stores/itemStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { ItemDTO, QueryItemDTO } from '@/common/types';

export const useItemStore = defineStore('item', {
  state: () => ({
    // 物品列表
    itemList: [] as ItemDTO[],
    
    // 当前选中的物品详情
    currentItem: null as ItemDTO | null,
    
    // 分页信息
    pagination: {
      currentPage: 1,
      pageSize: 10,
      total: 0
    },
    
    // 查询参数
    queryParams: {
      boxId: undefined as number | undefined,
      itemCode: '',
      itemTag: '',
      isValid: undefined as number | undefined
    } as QueryItemDTO,
    
    // 加载状态
    loading: false,
    detailLoading: false,
    
    // 选中物品ID（用于详情页）
    selectedItemId: null as number | null,
    
    // 当前盒子ID（用于筛选）
    currentBoxId: null as number | null
  }),

  getters: {
    // 有效物品（在盒内）
    validItems: (state) => state.itemList.filter(item => item.isValid === 1),
    
    // 已取出物品
    removedItems: (state) => state.itemList.filter(item => item.isValid === 0),
    
    // 根据盒子ID获取物品
    getItemsByBoxId: (state) => (boxId: number) => {
      return state.itemList.filter(item => item.boxId === boxId);
    },
    
    // 根据ID获取物品
    getItemById: (state) => (id: number) => {
      return state.itemList.find(item => item.id === id) || null;
    },
    
    // 搜索物品（根据名称或标签）
    searchItems: (state) => (keyword: string) => {
      if (!keyword.trim()) return state.itemList;
      
      const lowerKeyword = keyword.toLowerCase();
      return state.itemList.filter(item => 
        (item.autoRecognizeName?.toLowerCase().includes(lowerKeyword) ||
         item.manualEditName?.toLowerCase().includes(lowerKeyword) ||
         item.itemTag?.toLowerCase().includes(lowerKeyword) ||
         item.itemCode?.toLowerCase().includes(lowerKeyword))
      );
    },
    
    // 按标签分组
    itemsByTag: (state) => {
      const grouped: Record<string, ItemDTO[]> = {};
      state.itemList.forEach(item => {
        const tag = item.itemTag || '未分类';
        if (!grouped[tag]) {
          grouped[tag] = [];
        }
        grouped[tag].push(item);
      });
      return grouped;
    }
  },

  actions: {
    // 获取物品列表
    async fetchItemList(params?: QueryItemDTO) {
      this.loading = true;
      try {
        const response = await API.getItemList(params || this.queryParams);
        this.itemList = response.data || [];
        
        // 如果有分页信息
        if (response.data && response.data.pagination) {
          this.pagination = {
            currentPage: response.data.currentPage || 1,
            pageSize: response.data.pageSize || 10,
            total: response.data.total || 0
          };
        }
        
        return this.itemList;
      } catch (error) {
        console.error('获取物品列表失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 获取物品详情
    async fetchItemDetail(id: number) {
      this.detailLoading = true;
      this.selectedItemId = id;
      
      try {
        const response = await API.getItemDetail(id);
        this.currentItem = response.data;
        return response.data;
      } catch (error) {
        console.error(`获取物品详情失败 (ID: ${id}):`, error);
        throw error;
      } finally {
        this.detailLoading = false;
      }
    },

    // 创建或更新物品
    async saveItem(itemData: ItemDTO) {
      this.loading = true;
      try {
        await API.saveItem(itemData);
        
        // 更新本地数据
        if (itemData.id) {
          // 更新操作
          const index = this.itemList.findIndex(item => item.id === itemData.id);
          if (index !== -1) {
            this.itemList[index] = { ...this.itemList[index], ...itemData };
          }
          
          // 如果当前查看的就是这个物品，也更新
          if (this.currentItem?.id === itemData.id) {
            this.currentItem = { ...this.currentItem, ...itemData };
          }
        } else {
          // 创建操作 - 重新获取列表
          await this.fetchItemList();
        }
        
        return { success: true };
      } catch (error) {
        console.error('保存物品失败:', error);
        return { success: false, message: '保存失败' };
      } finally {
        this.loading = false;
      }
    },

    // 删除物品
    async deleteItem(id: number) {
      this.loading = true;
      try {
        await API.deleteItem(id);
        
        // 从列表中移除
        this.itemList = this.itemList.filter(item => item.id !== id);
        
        // 如果删除的是当前查看的物品，清空currentItem
        if (this.currentItem?.id === id) {
          this.currentItem = null;
        }
        
        return { success: true };
      } catch (error) {
        console.error(`删除物品失败 (ID: ${id}):`, error);
        return { success: false, message: '删除失败' };
      } finally {
        this.loading = false;
      }
    },

    // 更新查询参数
    updateQueryParams(params: Partial<QueryItemDTO>) {
      this.queryParams = { ...this.queryParams, ...params };
      if (params.boxId !== undefined) {
        this.currentBoxId = params.boxId;
      }
    },

    // 重置查询参数
    resetQueryParams() {
      this.queryParams = {
        boxId: undefined,
        itemCode: '',
        itemTag: '',
        isValid: undefined
      };
      this.currentBoxId = null;
    },

    // 清空当前物品
    clearCurrentItem() {
      this.currentItem = null;
      this.selectedItemId = null;
    },

    // 设置当前盒子（用于筛选）
    setCurrentBoxId(boxId: number | null) {
      this.currentBoxId = boxId;
      this.queryParams.boxId = boxId;
    },

    // 标记物品为已取出
    async markItemAsRemoved(itemId: number) {
      const item = this.itemList.find(item => item.id === itemId);
      if (item) {
        item.isValid = 0;
        // 可以调用API更新状态
        // await API.updateItemStatus(itemId, 0);
      }
    },

    // 标记物品为在盒内
    async markItemAsValid(itemId: number) {
      const item = this.itemList.find(item => item.id === itemId);
      if (item) {
        item.isValid = 1;
        // 可以调用API更新状态
        // await API.updateItemStatus(itemId, 1);
      }
    }
  }
});