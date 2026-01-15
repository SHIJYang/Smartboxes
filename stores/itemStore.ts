// stores/itemStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { ItemDTO, QueryItemDTO, PageQueryDTO } from '@/common/types';
// [新增] 引入 userStore
import { useUserStore } from './userStore';

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
      itemCode: undefined as string | undefined,
      name: undefined as string | undefined, 
      itemTag: undefined as string | undefined,
      isValid: undefined as number | undefined
    } as QueryItemDTO,
    
    loading: false,
    detailLoading: false
  }),

  getters: {
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
    
    getItemsByBoxId: (state) => (boxId: number) => {
        return state.itemList.filter(i => i.boxId === boxId && i.isValid === 1);
    },

    getItemsByBoxIds: (state) => (boxIds: number[]) => {
        return state.itemList.filter(i => boxIds.includes(i.boxId) && i.isValid === 1);
    }
  },

  actions: {
    async fetchItemPage(params: QueryItemDTO & Partial<PageQueryDTO> = {}) {
      this.loading = true;

      if (params.page !== undefined) this.pagination.page = params.page;
      if (params.size !== undefined) this.pagination.size = params.size;

      const { page, size, ...filters } = params;
      this.updateQueryParams(filters);

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

    // [修改] 根据当前登录用户获取所有物品
    async fetchUserItems() {
      const userStore = useUserStore();
      const userId = userStore.userId;

      if (!userId) {
          console.warn('fetchUserItems: 用户未登录');
          return;
      }

      this.resetQueryParams();
      // 这里直接设置 userId 并请求
      return await this.fetchItemPage({ userId, size: 20 });
    },

    async fetchItemsByBox(boxId: number) {
        this.resetQueryParams();
        return await this.fetchItemPage({ boxId, size: 20 });
    },
    
    async fetchAllItems(params: QueryItemDTO = {}) {
        this.loading = true;
        this.updateQueryParams(params);
        try {
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

    async fetchItemDetail(id: number) {
      this.detailLoading = true;
      const localItem = this.itemList.find(i => i.id === id);
      if (localItem) {
        this.currentItem = { ...localItem };
      }

      try {
        const res = await API.getItemDetail(id);
        if (res.code === 200) {
          this.currentItem = res.data;
          this._updateLocalItem(id, res.data);
        }
      } catch (error) {
        console.warn('Fetch detail failed, using local data if available');
      } finally {
        this.detailLoading = false;
      }
    },

    async saveItem(itemData: ItemDTO) {
      this.loading = true;
      try {
        const isEdit = !!itemData.id;
        const apiCall = isEdit ? API.updateItem : API.addItem;
        const res = await apiCall(itemData);

        if (res.code === 200) {
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
    
    async moveItem(itemId: number, targetBoxId: number) {
        try {
            const res = await API.moveItem(itemId, targetBoxId);
            if (res.code === 200) {
                this._updateLocalItem(itemId, { boxId: targetBoxId });
                return { success: true };
            }
            return { success: false, message: res.msg };
        } catch (e) {
            return { success: false, message: '移动失败' };
        }
    },

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
        userId: undefined,
        boxId: undefined,
        itemCode: undefined,
        name: undefined,
        itemTag: undefined,
        isValid: undefined
      };
    },

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