<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            v-model="searchParams.keyword"
            placeholder="搜索宠物名称/编号"
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
            v-model="searchParams.mainAttr"
            placeholder="主属性筛选"
            clearable
            style="width: 150px; margin-left: 10px"
            @change="loadData"
          >
            <el-option
              v-for="attr in attributeOptions"
              :key="attr"
              :label="attr"
              :value="attr"
            />
          </el-select>
          <el-button type="primary" @click="loadData" style="margin-left: 10px">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </div>
        <div class="toolbar-right">
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>新增宠物
          </el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" width="120" />
        <el-table-column prop="petNo" label="编号" width="100" />
        <el-table-column prop="mainAttr" label="主属性" width="90">
          <template #default="{ row }">
            <el-tag>{{ row.mainAttr }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subAttr" label="副属性" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.subAttr" type="info">{{ row.subAttr }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="hp" label="HP" width="70" />
        <el-table-column prop="attack" label="攻击" width="70" />
        <el-table-column prop="defense" label="防御" width="70" />
        <el-table-column prop="spAttack" label="魔攻" width="70" />
        <el-table-column prop="spDefense" label="魔防" width="70" />
        <el-table-column prop="speed" label="速度" width="70" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="searchParams.page"
          v-model:page-size="searchParams.size"
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
      :title="isEdit ? '编辑宠物' : '新增宠物'"
      width="700px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编号" prop="petNo">
              <el-input v-model="form.petNo" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主属性" prop="mainAttr">
              <el-select v-model="form.mainAttr" placeholder="请选择主属性" style="width: 100%">
                <el-option v-for="attr in attributeOptions" :key="attr" :label="attr" :value="attr" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="副属性" prop="subAttr">
              <el-select v-model="form.subAttr" placeholder="请选择副属性" clearable style="width: 100%">
                <el-option v-for="attr in attributeOptions" :key="attr" :label="attr" :value="attr" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="HP" prop="hp">
              <el-input-number v-model="form.hp" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="攻击" prop="attack">
              <el-input-number v-model="form.attack" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="防御" prop="defense">
              <el-input-number v-model="form.defense" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="魔攻" prop="spAttack">
              <el-input-number v-model="form.spAttack" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="魔防" prop="spDefense">
              <el-input-number v-model="form.spDefense" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="速度" prop="speed">
              <el-input-number v-model="form.speed" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="技能">
          <el-select
            v-model="form.skillIds"
            multiple
            filterable
            placeholder="请选择技能"
            style="width: 100%"
          >
            <el-option
              v-for="skill in skillOptions"
              :key="skill.id"
              :label="skill.name"
              :value="skill.id"
            />
          </el-select>
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
const skillOptions = ref<any[]>([])

const attributeOptions = [
  '火', '水', '草', '电', '冰', '翼', '毒', '土',
  '武', '普通', '幽灵', '恶魔', '龙', '机械', '光', '萌'
]

const searchParams = reactive({
  keyword: '',
  mainAttr: '',
  page: 1,
  size: 10
})

const defaultForm = {
  id: null as number | null,
  name: '',
  petNo: '',
  mainAttr: '',
  subAttr: '',
  hp: 0,
  attack: 0,
  defense: 0,
  spAttack: 0,
  spDefense: 0,
  speed: 0,
  skillIds: [] as number[]
}

const form = reactive({ ...defaultForm })

const rules: FormRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  petNo: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  mainAttr: [{ required: true, message: '请选择主属性', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/pet/list', { params: searchParams })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    // Error handled by interceptor
  } finally {
    loading.value = false
  }
}

async function loadSkillOptions() {
  try {
    const res: any = await request.get('/skill/list', { params: { page: 1, size: 999 } })
    skillOptions.value = res.data.records || []
  } catch (error) {
    // ignore
  }
}

function openDialog(row?: any) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      petNo: row.petNo,
      mainAttr: row.mainAttr,
      subAttr: row.subAttr || '',
      hp: row.hp || 0,
      attack: row.attack || 0,
      defense: row.defense || 0,
      spAttack: row.spAttack || 0,
      spDefense: row.spDefense || 0,
      speed: row.speed || 0,
      skillIds: row.skillIds || []
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
      await request.put(`/pet/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/pet', form)
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
    await ElMessageBox.confirm('确定要删除该宠物吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/pet/${id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // Cancel or error
  }
}

onMounted(() => {
  loadData()
  loadSkillOptions()
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
