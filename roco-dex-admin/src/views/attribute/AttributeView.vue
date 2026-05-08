<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <h3>属性克制关系表</h3>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="attackAttr" label="攻击属性" width="120">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.attackAttr }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="defenseAttr" label="防御属性" width="120">
          <template #default="{ row }">
            <el-tag type="primary">{{ row.defenseAttr }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="multiplier" label="倍率" width="120">
          <template #default="{ row }">
            <el-tag :type="getMultiplierType(row.multiplier)">
              {{ row.multiplier }}x
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑倍率</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="编辑克制倍率"
      width="400px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="攻击属性">
          <el-input :model-value="form.attackAttr" disabled />
        </el-form-item>
        <el-form-item label="防御属性">
          <el-input :model-value="form.defenseAttr" disabled />
        </el-form-item>
        <el-form-item label="倍率" prop="multiplier">
          <el-input-number
            v-model="form.multiplier"
            :min="0"
            :max="10"
            :step="0.5"
            :precision="1"
            style="width: 100%"
          />
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
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  attackAttr: '',
  defenseAttr: '',
  multiplier: 1.0
})

const rules: FormRules = {
  multiplier: [{ required: true, message: '请输入倍率', trigger: 'blur' }]
}

function getMultiplierType(multiplier: number) {
  if (multiplier > 1) return 'danger'
  if (multiplier < 1) return 'info'
  return 'success'
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/attribute/table')
    tableData.value = res.data || []
  } catch (error) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

function openEditDialog(row: any) {
  form.id = row.id
  form.attackAttr = row.attackAttr
  form.defenseAttr = row.defenseAttr
  form.multiplier = row.multiplier
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await request.put(`/attribute/${form.id}`, { multiplier: form.multiplier })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    // Error handled by interceptor
  } finally {
    submitLoading.value = false
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

    h3 {
      margin: 0;
      color: #303133;
    }
  }
}
</style>
