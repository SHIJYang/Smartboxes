// stores/itemStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { ItemDTO, QueryItemDTO } from '@/common/types';

export const useItemStore = defineStore('item', {
  state: () => ({
    itemList: [] as ItemDTO[],
    currentItem: null as ItemDTO | null,
    
    pagination: {
      page: 1,
      size: 10,
      total: 0
    },
    
    queryParams: {
      userId: undefined as number | undefined,
      boxId: undefined as number | undefined,
      itemCode: '',
      name: '', 
      itemTag: '',
      isValid: undefined as number | undefined
    } as QueryItemDTO,
    
    loading: false,
    detailLoading: false
  }),

  getters: {
    // 纯前端搜索 (Getter): 当列表已加载时快速筛选
    searchLocalItems: (state) => (keyword: string) => {
      if (!keyword) return state.itemList;
      const lower = keyword.toLowerCase();
      return state.itemList.filter(item => 
        (item.autoRecognizeName?.toLowerCase().includes(lower) ||
         item.manualEditName?.toLowerCase().includes(lower) ||
         item.itemTag?.toLowerCase().includes(lower) ||
         item.itemCode?.toLowerCase().includes(lower))
      );
    },
    
    // 根据 BoxId 筛选 (用于盒子详情页)
    getItemsByBoxId: (state) => (boxId: number) => {
        return state.itemList.filter(i => i.boxId === boxId && i.isValid === 1);
    }
  },

  actions: {
    // 1. 获取物品分页
    async fetchItemPage(params: QueryItemDTO = {}) {
      this.loading = true;
      this.updateQueryParams(params);

      try {
        const res = await API.getItemPage({ 
            ...this.queryParams, 
            page: this.pagination.page,
            size: this.pagination.size 
        });
        
        if (res.code === 200 && res.data) {
          this.itemList = res.data.data;
          this.pagination = {
            page: res.data.page,
            size: res.data.size,
            total: res.data.total
          };
        }
      } catch (error) {
        console.error('Fetch items error:', error);
      } finally {
        this.loading = false;
      }
    },
    
    // 2. 获取所有物品 (用于无需分页的场景，如搜索或统计)
    async fetchAllItems(params: QueryItemDTO = {}) {
        this.loading = true;
        this.updateQueryParams(params);
        try {
            // 调用无分页接口 list
            const res = await API.getItemList(this.queryParams);
            if (res.code === 200) {
                this.itemList = res.data;
                this.pagination.total = res.data.length;
            }
        } catch (e) {
            console.error(e);
        } finally {
            this.loading = false;
        }
    },

    // 3. 获取详情 [核心要求：优先本地]
    async fetchItemDetail(id: number) {
      this.detailLoading = true;
      
      // A. 优先从 Local List 获取，实现零延迟展示
      const localItem = this.itemList.find(i => i.id === id);
      if (localItem) {
        this.currentItem = { ...localItem };
      }

      // B. 后台静默刷新数据，保证数据时效性
      try {
        const res = await API.getItemDetail(id);
        if (res.code === 200) {
          this.currentItem = res.data;
          // 同步更新列表中的旧数据
          const idx = this.itemList.findIndex(i => i.id === id);
          if (idx !== -1) this.itemList[idx] = res.data;
        }
      } catch (error) {
        console.warn('Fetch detail failed, using local data if available');
      } finally {
        this.detailLoading = false;
      }
    },

    // 4. 保存
    async saveItem(itemData: ItemDTO) {
      this.loading = true;
      try {
        const isEdit = !!itemData.id;
        const apiCall = isEdit ? API.updateItem : API.addItem;
        const res = await apiCall(itemData);

        if (res.code === 200) {
          // 简单起见，重新拉取当前页
          await this.fetchItemPage(); 
          return { success: true };
        }
        return { success: false, message: res.msg };
      } catch (error) {
        return { success: false, message: '保存异常' };
      } finally {
        this.loading = false;
      }
    },

    // 5. 删除
    async deleteItem(id: number) {
      try {
        const res = await API.deleteItem(id);
        if (res.code === 200) {
          this.itemList = this.itemList.filter(i => i.id !== id);
          if (this.currentItem?.id === id) this.currentItem = null;
          this.pagination.total = Math.max(0, this.pagination.total - 1);
          return { success: true };
        }
        return { success: false, message: res.msg };
      } catch (e) {
        return { success: false, message: '删除失败' };
      }
    },
    
    // 6. 移动物品
    async moveItem(itemId: number, targetBoxId: number) {
        try {
            const res = await API.moveItem(itemId, targetBoxId);
            if (res.code === 200) {
                // 本地乐观更新
                this._updateLocalItem(itemId, { boxId: targetBoxId });
                return { success: true };
            }
            return { success: false, message: res.msg };
        } catch (e) {
            return { success: false, message: '移动失败' };
        }
    },

    // 7. 更新状态 (取出/放入)
    async updateItemStatus(itemId: number, isValid: number) {
        try {
            const res = await API.updateItemStatus(itemId, isValid);
            if (res.code === 200) {
                this._updateLocalItem(itemId, { isValid });
                return { success: true };
            }
            return { success: false, message: res.msg };
        } catch (e) {
            return { success: false, message: '更新状态失败' };
        }
    },

    updateQueryParams(params: Partial<QueryItemDTO>) {
      this.queryParams = { ...this.queryParams, ...params };
    },
    
    resetQueryParams() {
      this.queryParams = {
        boxId: undefined,
        itemCode: '',
        name: '',
        itemTag: '',
        isValid: undefined
      };
    },

    // 内部辅助：更新本地数据
    _updateLocalItem(id: number, patch: Partial<ItemDTO>) {
        if (this.currentItem?.id === id) {
            this.currentItem = { ...this.currentItem, ...patch };
        }
        const idx = this.itemList.findIndex(i => i.id === id);
        if (idx !== -1) {
            this.itemList[idx] = { ...this.itemList[idx], ...patch };
        }
    }
  }
});