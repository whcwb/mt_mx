<template>
  <div>
    <Modal
        v-model="showModal"
        height="600"
        width="900"
        :closable='false'
        :mask-closable="false"
        title="新增套餐">
      <Form
          ref="form"
          :rules="ruleInline"
          :label-width="100"
          :styles="{top: '20px'}">
        <div style="overflow: auto;height: 400px;width:800px">
          <Col span="12">
            <FormItem prop="by5" label='套餐类型：'>
              <Select v-model="param.by5" @on-change="selectBy5">
                <Option v-for="item in tcList" :value="item.val"> {{ item.label }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem prop="by9" label='套餐名称：'>
              <Input v-model="param.by9" placeholder="套餐名称" clearable></Input>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem prop="zdmc" label='套餐单价:'>
              <Input v-model="param.zdmc" placeholder="套餐单价"></Input>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem prop="jgdm" label='所属考场:'>
              <Select v-model="param.jgdm" readonly>
                <Option v-for="item in JGList" :value="item.val">{{ item.label }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem prop="cx" label='训练车型:'>
              <CheckboxGroup v-model="cx" size="small" @on-change="checkAllGroupChange">
                <Checkbox v-for="item in cxList" :label="item.label" :value="item.label">{{ item.label }}</Checkbox>
              </CheckboxGroup>
            </FormItem>
          </Col>

          <Col span="12" v-if="param.by5=='00'">
            <FormItem prop="fd" label='返点率:'>
              <Input v-model="param.fd" placeholder="请输入返点率"> </Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="param.by5=='10'">
            <FormItem prop="fd" label='返点金额:'>
              <Input v-model="param.fd" placeholder="请输入返点金额"> </Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="param.by5=='10'">
            <FormItem prop="ljjs" label='是否立即结算:'>
              <RadioGroup v-model="param.ljjs">
                <Radio label="0" value="0">否</Radio>
                <Radio label="1" value="1">是</Radio>
              </RadioGroup>
            </FormItem>
          </Col>
        </div>
      </Form>
      <div slot='footer'>
        <Button type="default" @click="closeModal" style="color: #949494">取消</Button>
        <Button type="primary" @click="saveTc">确定</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
export default {
  name: "savetc",
  props: {
    jgdm: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      lx: '小时计费',
      showModal: true,
      ruleInline: {},
      tcList: [{val: '00', label: '计时'}, {val: '10', label: '按把'}, {val: '20', label: '培优'}],
      cxList: [{label: 'C1'}, {label: 'C2'}, {label: 'B2'}, {label: 'A1'}, {label: 'A2'}, {label: 'A3'}],
      param: {
        zdmc: '',
        by5: '',
        by9: '',
        jgdm: '',
        km: 'K3',
        fd: '',
        by10: '',
        qz: '',
        cx: '',
        ljjs: '0'
      },
      cx: [],
      JGList: [],
      social: []
    }
  },
  created() {
    this.param.jgdm = this.jgdm;
    this.getJgs();
  },
  methods: {
    checkAllGroupChange(val) {
      this.param.cx = val.join(',')
    },
    selectBy5(val) {
      if (val == '00') {
        this.param.by9 = '科三-计时'
      } else if (val == '10') {
        this.param.by9 = '科三-按把'
      } else if (val == '20') {
        this.param.by9 = '科三-培优'
      }
    },
    closeModal() {
      this.$parent.compName = ''
      this.$parent.getJgs();
    },
    saveTc() {
      this.$http.post("/api/lcjl/saveTc", this.param).then(res => {
        if (res.code == 200) {
          this.$Message.info(res.message);
          this.closeModal();
        } else {
          this.swal({
            title: res.message,
            type: 'warning'
          })
        }

      })

    },
    getJgs() {
      this.JGList = []
      this.$http.get("/api/lccl/getJgsByOrgCode").then(res => {
        for (let v of res.result) {
          let val = {val: v.jgdm, label: v.jgmc}
          this.JGList.push(val)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>