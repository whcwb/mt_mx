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
              <Select v-model="param.by5">
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
            <FormItem v-if="param.by5=='00'" label="套餐类型">
              <RadioGroup v-model="lx" style="width: 200px;">
                <Radio label="小时计费"></Radio>
                <Radio label="单人套餐"></Radio>
              </RadioGroup>
            </FormItem>
          </Col>
          <Col span="12" v-if="param.by5=='00'">
            <FormItem prop="fd" label='返点率:'>
              <Input v-model="param.fd" placeholder="请输入返点率"> </Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="param.by5=='20'">
            <FormItem prop="fd" label='返点金额:'>
              <Input v-model="param.fd" placeholder="请输入返点金额"> </Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="param.by5=='30'">
            <FormItem prop="fdje" label='返点金额:'>
              <Input v-model="param.fdje" placeholder="请输入返点金额"> </Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="param.by5=='30'">
            <FormItem prop="fd" label='超出返点率:'>
              <Input v-model="param.fd" placeholder="请输入返点率"> </Input>
            </FormItem>
          </Col>

          <Col span="12">
            <FormItem v-if="param.by5 == '30'" label="抵扣时长:">
              <Input v-model="param.by10" placeholder="开放日抵扣时长"></Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="lx=='单人套餐'">
            <FormItem label="超出单价(分钟):">
              <Input v-model="param.by3" placeholder="超出时长单价(分钟)" style="width: "></Input>
            </FormItem>
          </Col>
          <Col span="12" v-if="lx=='单人套餐'">
            <FormItem label="套餐时长:">
              <Input v-model="param.qz" placeholder="套餐时长" style="width: 150px"></Input>
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
      tcList: [{val: '00', label: '计时'}, {val: '20', label: '培优'}, {val: '30', label: '开放'}],
      param: {
        zdmc: '',
        by5: '',
        by9: '',
        jgdm: '',
        km: 'K2',
        fd: '',
        by10: '',
        qz: ''
      },
      JGList: []
    }
  },
  created() {
    this.param.jgdm = this.jgdm;
    this.getJgs();
  },
  methods: {
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