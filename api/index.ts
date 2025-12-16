import { request } from '@/utils/request';
import type { BoxDTO, ItemDTO, UserDTO, AiChatResponse, AiChatRequestDTO } from '@/common/types';
import * as M from '@/mock/data';

// --- 盒子相关 ---
export const getBoxList = (userId: number) => request<BoxDTO[]>('/api/boxes/list', 'GET', { userId }, M.mockBoxList);
export const getBoxDetail = (id: number) => request<BoxDTO>(`/api/boxes/${id}`, 'GET', {}, M.mockBoxDetail);
export const saveBox = (data: BoxDTO) => request<void>(data.id ? '/api/boxes/update' : '/api/boxes/add', data.id ? 'PUT' : 'POST', data, M.mockSuccess);
export const deleteBox = (id: number) => request<void>(`/api/boxes/delete/${id}`, 'DELETE', {}, M.mockSuccess);

// --- 物品相关 ---
export const getItemList = (params: any) => request<ItemDTO[]>('/api/items/list', 'GET', params, M.mockItemList);
export const getItemDetail = (id: number) => request<ItemDTO>(`/api/items/${id}`, 'GET', {}, M.mockItemDetail); // 需自行在mock加id参数判断逻辑，此处简化
export const saveItem = (data: ItemDTO) => request<void>(data.id ? '/api/items/update' : '/api/items/add', data.id ? 'PUT' : 'POST', data, M.mockSuccess);
export const deleteItem = (id: number) => request<void>(`/api/items/delete/${id}`, 'DELETE', {}, M.mockSuccess);

// --- 用户与AI ---
export const login = (id: number) => request<UserDTO>(`/api/users/${id}`, 'GET', {}, M.mockUser);
export const sendChat = (data: AiChatRequestDTO) => request<AiChatResponse>('/api/chat', 'POST', data, M.mockChat);