<template>
  <div style="min-height: 300px">
    <!--新增教练员-->
    <Card>
      <Form :model="formDataJL">
        <Row :gutter="32">
          <Col span="24">
            <Col span="4">
              <FormItem label="教练员姓名" label-position="top" style="width: 95%">
                <Input v-model="formDataJL.jlXm"/>
              </FormItem>
            </Col>
            <Col span="5">
              <FormItem label="教练员联系方式" label-position="top" style="width: 95%">
                <Input v-model="formDataJL.jlLxdh"/>
              </FormItem>
            </Col>
            <Col span="6">
              <FormItem label="所属驾校" label-position="top" style="width: 95%">
                <Select filterable clearable v-model="formDataJL.jlJx" :label-in-value="true" @on-change="jsSelect">
                  <Option v-for="(item,index) in schoolList" :key="index" :value="item.val">{{item.val}}-{{item.by1}}</Option>
                </Select>
              </FormItem>
            </Col>
            <Col span="4">
              <FormItem label="驾校" label-position="top" style="width: 95%;">
                <RadioGroup v-model="formDataJL.jlLx">
                  <Radio label="00"  @click="formDataJL.jlLx='00'"> 本校</Radio>
                  <Radio label="10" @click="formDataJL.jlLx='10'">外校</Radio>
                  </RadioGroup>
              </FormItem>
            </Col>
            <Col span="3">
              <FormItem label-position="top" style="width: 95%">
                <div slot="label" style="color: #fff">_________</div>
                <Button type="primary" @click="wxjlSave">新增</Button>
              </FormItem>
            </Col>
            <Col span="2" v-if="showCloseBtn">
              <FormItem label-position="top"  style="width: 95%">
                <div slot="label" style="color: #fff">_________</div>
                <Button type="warning" @click="gogo">
                  关闭
                </Button>
              </FormItem>
            </Col>
          </Col>

        </Row>
      </Form>
      <jlwh ref="jlwh"
            @JLRowClick="JLRowClick"
            @remove="remove"
            :Tabheight="AF.getPageHeight()-800"
            :parms="{'jlJx':formDataJL.jlJx,notShowLoading:'true'}"></jlwh>
    </Card>
  </div>
</template>

<script>
  import jlwh from './jlWh'

  export default {
    name: "addJL",
    components: {jlwh},
    props: {
      jxmc: {
        type: String,
        default: ''
      },
      showCloseBtn: {
        type: Boolean,
        default: true
      },
    },
    data() {
      return {
        schoolList: [],
        formDataJL: {
          jlJx: '',
          jlXm: '',
          jlLxdh: '',
          jlLx:'00'
        }
      }
    },
    watch: {
      'formDataJL.jlJx': function (n, o) {
        this.$emit('jxSeljxSel', n)
      },
      // jxmc: function (n, o) {
      //   this.formDataJL.jlJx = n
      // }
    },
    created() {
      this.formDataJL.jlJx = this.jxmc
      this.getSchoolList();
    },
    methods: {
      jsSelect(val) {
        // this.formDataJL.jlJx = val.value
        // console.log(val.value);
        // console.log(this.formDataJL);
      },
      remove() {
        this.$emit('remove')
      },
      getSchoolList() {
        this.schoolList = [];
        let list = this.dictUtil.getByCode(this, 'ZDCLK1017')
        // let t = [];
        // for (let r of list) {
        //   // let py = this.util.parsePY(r.val);
        //   t.push({label: r.val + '-' + r.by1, value: r.val})
        // }
        this.schoolList = list;
        console.log(this.schoolList);
      },
      JLRowClick(row) {
        this.$emit('JLRowClick', row)
      },
      gogo() {
        this.$emit('colse')
      },
      addWx() {
        this.$emit('addWx')
      },
      wxjlSave() {
        if (this.formDataJL.jlJx == '') {
          this.swal({
            title: '请选择驾校!',
            type: 'warning'
          })
          return
        }
        if (this.formDataJL.jlXm == '') {
          this.swal({
            title: '请填写教练姓名!',
            type: 'warning'
          })
          return
        }
        if(this.formDataJL.jlLxdh == ''){
          this.swal({
            title:'请填写教练电话!',
            type:'warning'
          })
          return
        }
        console.log('######',this.formDataJL);
        let params = JSON.parse(JSON.stringify(this.formDataJL));
        this.$http.post('/api/lcwxjl/save', params).then(res => {
          if (res.code == 200) {
            this.swal({
              title: '新增完成',
              type: 'success'
            })
            this.formDataJL.jlXm = ''
            this.formDataJL.jlLxdh = ''
            this.$refs['jlwh'].getPagerList()
            this.$emit('SaveOk', res.result)
          }else {
            this.swal({
              title:res.message,
              type:'error'
            })
          }
        }).catch(err => {
        })
      }
    }
  }
</script>

<style scoped>

</style>
