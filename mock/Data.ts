import type { RestResult, BoxDTO, ItemDTO, UserDTO, AiChatResponse } from '@/common/types';

export const mockBoxList: RestResult<BoxDTO[]> = {
  code: 200, message: "success",
  data: [
    { id: 1, boxName: "客厅杂物箱", boxCode: "BOX_001", boxType: 1, status: 1, userId: 1001, battery: 80 },
    { id: 2, boxName: "卧室衣柜", boxCode: "BOX_002", boxType: 0, status: 0, userId: 1001, battery: 30 },
    { id: 3, boxName: "书房书架", boxCode: "BOX_003", boxType: 1, status: 1, userId: 1001, battery: 90 },
    { id: 4, boxName: "厨房储物柜", boxCode: "BOX_004", boxType: 0, status: 1, userId: 1001, battery: 60 }
  ]
};

export const mockBoxDetail: RestResult<BoxDTO> = {
  code: 200, message: "success",
  data: { id: 1, boxName: "客厅杂物箱", boxCode: "BOX_001", boxType: 1, status: 1, userId: 1001, battery: 80 }
};

export const mockItemList: RestResult<ItemDTO[]> = {
  code: 200, message: "success",
  data: [
    { id: 101, boxId: 1, itemCode: "ITM_001", itemName: "Switch游戏机", price: 2000, itemTag: "数码", isValid: 1, createTime: "2023-01-15" },
    { id: 102, boxId: 1, itemCode: "ITM_002", itemName: "充电宝", price: 100, itemTag: "数码", isValid: 1, createTime: "2023-02-20" },
    { id: 103, boxId: 2, itemCode: "ITM_003", itemName: "冬季外套", price: 500, itemTag: "衣物", isValid: 1, createTime: "2023-01-10" },
    { id: 104, boxId: 2, itemCode: "ITM_004", itemName: "牛仔裤", price: 200, itemTag: "衣物", isValid: 1, createTime: "2023-03-05" },
    { id: 105, boxId: 3, itemCode: "ITM_005", itemName: "JavaScript高级程序设计", price: 120, itemTag: "书籍", isValid: 1, createTime: "2023-02-15" },
    { id: 106, boxId: 3, itemCode: "ITM_006", itemName: "笔记本电脑", price: 8000, itemTag: "数码", isValid: 1, createTime: "2023-01-20" },
    { id: 107, boxId: 4, itemCode: "ITM_007", itemName: "调料套装", price: 80, itemTag: "厨具", isValid: 1, createTime: "2023-03-10" }
  ]
};

export const mockItemDetail: RestResult<ItemDTO> = {
  code: 200, message: "success",
  data: { id: 101, boxId: 1, itemCode: "ITM_001", itemName: "Switch游戏机", price: 2000, itemTag: "数码", itemDesc: "红蓝手柄", isValid: 1, createTime: "2023-01-15" }
};

export const mockUser: RestResult<UserDTO> = {
  code: 200, message: "success",
  data: { id: 1001, username: "Admin", email: "admin@test.com", avatar: "/static/logo.png" }
};

export const mockChat: RestResult<AiChatResponse> = {
  code: 200, message: "success",
  data: { reply: "我帮你查到了，这个物品在【客厅杂物箱】里。" }
};

export const mockSuccess: RestResult<any> = { code: 200, message: "操作成功", data: null };