<template>
  <div style="padding-top: 8px;width: 100%;border-collapse: collapse">
    <tr
        style="text-align: center;height:30px;font-size: 14px;border-bottom: 1px solid #DCDEE2;padding: 10px;border-collapse: collapse">
      <td style="min-width:89px;">{{ index + 1 }}</td>
      <td style="min-width: 107px;">{{ source.lcKm == '2' ? '科目二' : '科目三' }}</td>
      <td style="min-width: 119px;">{{ source.jlJx }}</td>
      <td style="min-width: 107px;">{{ source.jlXm }}</td>
      <td style="min-width: 109px;">{{ source.jlCx }}</td>
      <td style="min-width: 147px;">{{ source.zdxm.by9 + '-' + source.zdxm.zdmc }}</td>
      <td style="min-width: 100px;">{{ source.xySl }}</td>
      <td style="min-width: 161px;">{{ source.kssj }}</td>
      <td style="min-width: 129px;">{{ source.jssj.substring(10) }}</td>
      <td style="min-width: 127px;">{{ source.sc }}</td>
      <td style="min-width: 100px;">{{ source.lcFy + " 元" }}</td>
      <td style="min-width: 96px;">{{ source.xjje + ' 元' }}</td>
      <td style="min-width: 129px;">{{ source.zffs }}</td>
      <td style="min-width: 100px;padding-left: 15px">{{ source.zgXm }}</td>
    </tr>

  </div>
</template>

<script>
export default {
  name: 'item-component',
  props: {
    index: { // index of current item
      type: Number,
      default() {
        return 1;
      }
    },
    source: { // here is: {uid: 'unique_1', text: 'abc'}
      type: Object,
      default() {
        return {}
      }
    }
  }
}
</script>
