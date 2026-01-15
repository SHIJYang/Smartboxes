// stores/boxStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { BoxDTO, QueryBoxDTO } from '@/common/types';
// [新增] 引入 userStore 以获取当前登录用户ID
import { useUserStore } from './userStore';

export const useBoxStore = defineStore('box', {
  state: () => ({
    boxList: [] as BoxDTO[],
    currentBox: null as BoxDTO | null,
    
    pagination: {
      page: 1,
      size: 10,
      total: 0
    },
    
    // 查询条件持久化
    queryParams: {
      userId: undefined as number | undefined,
      boxCode: undefined as string | undefined, 
      boxName: undefined as string | undefined,
      status: undefined as number | undefined
    } as QueryBoxDTO,
    
    loading: false,
    detailLoading: false
  }),

  getters: {
    onlineBoxes: (state) => state.boxList.filter(box => box.status === 1),
    offlineBoxes: (state) => state.boxList.filter(box => box.status === 0),
    mainBoxes: (state) => state.boxList.filter(box => box.boxType === 1),
    currentBoxIds: (state) => state.boxList.map(b => b.id).filter((id): id is number => !!id),
  },

  actions: {
    // 1. 获取盒子列表 (分页)
    async fetchBoxList(params: QueryBoxDTO = {}) {
      this.loading = true;
      // 合并查询参数
      this.queryParams = { ...this.queryParams, ...params };

      try {
        const res = await API.getBoxPage({ 
            ...this.queryParams, 
            page: this.pagination.page, 
            size: this.pagination.size 
        });

        if (res.code === 200 && res.data) {
          this.boxList = res.data.data;
          this.pagination = {
            page: res.data.page,
            size: res.data.size,
            total: res.data.total
          };
        }
        return this.boxList;
      } catch (error) {
        console.error('Fetch box list error:', error);
      } finally {
        this.loading = false;
      }
    },

    // [修改] 根据当前登录用户获取盒子
    async fetchUserBoxes() {
      const userStore = useUserStore();
      const userId = userStore.userId;

      if (!userId) {
        console.warn('fetchUserBoxes: 用户未登录');
        return [];
      }

      // 重置其他查询条件，锁定 userId
      this.queryParams = {
        userId: userId,
        boxCode: undefined,
        boxName: undefined,
        status: undefined
      };
      
      return await this.fetchBoxList({ userId });
    },

    // 2. 获取详情 (智能回退策略)
    async fetchBoxDetail(id: number) {
      this.detailLoading = true;
      
      // A. 优先查本地缓存
      const localBox = this.boxList.find(b => b.id === id);
      if (localBox) {
        this.currentBox = { ...localBox };
        this.detailLoading = false;
        return;
      }

      // B. 尝试调用 Mock/兼容接口
      try {
        const res = await API.getBoxDetailMock(id);
        if (res.code === 200) {
          this.currentBox = res.data;
          this._upsertList(res.data);
          return;
        }
      } catch (e) {
        console.warn('API getBoxDetailMock failed, trying fallback...');
      }

      // C. 回退策略
      try {
        await this.fetchBoxList({ size: 50 }); 
        const found = this.boxList.find(b => b.id === id);
        if (found) {
            this.currentBox = { ...found };
        } else {
            console.error('Box not found in list');
            this.currentBox = null;
        }
      } finally {
        this.detailLoading = false;
      }
    },

    // 3. 保存 (新增/修改)
    async saveBox(boxData: BoxDTO) {
      this.loading = true;
      try {
        let res;
        // 如果没有userId，尝试自动填充
        if (!boxData.userId) {
            const userStore = useUserStore();
            if (userStore.userId) boxData.userId = userStore.userId;
        }

        if (boxData.id) {
          res = await API.updateBox(boxData);
        } else {
          res = await API.addBox(boxData);
        }

        if (res.code === 200) {
          await this.fetchBoxList(); 
          return { success: true };
        }
        return { success: false, message: res.msg };
      } catch (e) {
        return { success: false, message: '操作失败' };
      } finally {
        this.loading = false;
      }
    },

    // 4. 删除
    async deleteBox(id: number) {
      try {
        const res = await API.deleteBox(id);
        if (res.code === 200) {
          this.boxList = this.boxList.filter(b => b.id !== id);
          if (this.currentBox?.id === id) this.currentBox = null;
          this.pagination.total = Math.max(0, this.pagination.total - 1);
          return { success: true };
        }
        return { success: false, message: res.msg };
      } catch (e) {
        return { success: false, message: '删除失败' };
      }
    },
    
    _upsertList(box: BoxDTO) {
        const idx = this.boxList.findIndex(b => b.id === box.id);
        if (idx !== -1) {
            this.boxList[idx] = box;
        } else {
            this.boxList.push(box);
        }
    }
  }
});