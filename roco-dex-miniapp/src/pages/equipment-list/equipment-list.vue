<template>
  <view class="page-equipment-list">
    <search-bar v-model="keyword" placeholder="搜索装备名称" @search="onSearch" />

    <view class="equip-grid">
      <view
        v-for="equip in equipList"
        :key="equip.id"
        class="equip-card"
        @tap="goDetail(equip.id)"
      >
        <image
          class="equip-image"
          :src="equip.imageUrl || '/static/images/default.png'"
          mode="aspectFit"
        />
        <text class="equip-name">{{ equip.name }}</text>
        <text class="equip-category">{{ equip.category }}</text>
      </view>
    </view>

    <view v-if="loading" class="loading"><text>加载中...</text></view>
    <view v-if="!loading && equipList.length === 0" class="empty"><text>暂无装备数据</text></view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import SearchBar from '@/components/search-bar/search-bar.vue'

const keyword = ref('')
const equipList = ref<any[]>([])
const loading = ref(false)

onLoad(() => {
  loadEquipments()
})

async function loadEquipments() {
  loading.value = true
  try {
    const res = await get<any>('/api/equipment/list', { page: 1, size: 50 })
    equipList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function onSearch() {
  if (!keyword.value.trim()) {
    loadEquipments()
    return
  }
  loading.value = true
  try {
    const res = await get<any>('/api/equipment/search', { keyword: keyword.value, page: 1, size: 50 })
    equipList.value = res.data.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/equipment-detail/equipment-detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.page-equipment-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.equip-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 16rpx;
}

.equip-card {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0;
  box-sizing: border-box;
}

.equip-image {
  width: 100rpx;
  height: 100rpx;
  border-radius: 16rpx;
  background: #fff;
}

.equip-name {
  font-size: 24rpx;
  color: #333;
  margin-top: 8rpx;
  text-align: center;
}

.equip-category {
  font-size: 20rpx;
  color: #999;
  margin-top: 4rpx;
}

.loading, .empty {
  text-align: center;
  padding: 40rpx;
  color: #999;
}
</style>
