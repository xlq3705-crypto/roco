<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchParams.keyword"
            placeholder="搜索管理员/模块"
            clearable
            style="width: 220px"
            @clear="loadData"
            @keyup.enter="loadData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="loadData" style="margin-left: 10px">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="adminUsername" label="管理员" width="120" />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作" width="100" />
        <el-table-column prop="targetId" label="目标ID" width="90" />
        <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="createTime" label="操作时间" min-width="180" />
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="searchParams.pageNum"
          v-model:page-size="searchParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

const searchParams = reactive({
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/log/operation/list', { params: searchParams })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.page-container {
  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .toolbar-left {
      display: flex;
      align-items: center;
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
