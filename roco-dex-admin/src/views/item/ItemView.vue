<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchParams.keyword"
            placeholder="搜索道具名称"
            clearable
            style="width: 220px"
            @clear="loadData"
            @keyup.enter="loadData"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select
            v-model="searchParams.category"
            placeholder="分类筛选"
            clearable
            style="width: 150px; margin-left: 10px"
            @change="loadData"
          >
            <el-option label="进化道具" value="进化道具" />
            <el-option label="活动道具" value="活动道具" />
            <el-option label="其他" value="其他" />
          </el-select>
          <el-button type="primary" @click="loadData" style="margin-left: 10px">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>
        <div class="toolbar-right">
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>新增道具
          </el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag :type="row.category === '进化道具' ? 'success' : row.category === '活动道具' ? 'warning' : 'info'">
              {{ row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="obtainMethod" label="获取方式" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑道具' : '新增道具'"
      width="600px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入道具名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="进化道具" value="进化道具" />
            <el-option label="活动道具" value="活动道具" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="获取方式" prop="obtainMethod">
          <el-input v-model="form.obtainMethod" type="textarea" :rows="2" placeholder="请输入获取方式" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const tableData = ref<any[]>([])
const total = ref(0)

const searchParams = reactive({
  keyword: '',
  category: '',
  pageNum: 1,
  pageSize: 10
})

const defaultForm = {
  id: null as number | null,
  name: '',
  category: '',
  description: '',
  obtainMethod: ''
}

const form = reactive({ ...defaultForm })

const rules: FormRules = {
  name: [{ required: true, message: '请输入道具名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/item/list', { params: searchParams })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

function openDialog(row?: any) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      category: row.category,
      description: row.description || '',
      obtainMethod: row.obtainMethod || ''
    })
  } else {
    Object.assign(form, defaultForm)
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await request.put(`/item/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/item', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    // Error handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除该道具吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/item/${id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // Cancel or error
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
