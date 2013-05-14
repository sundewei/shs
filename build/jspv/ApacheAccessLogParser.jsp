<tr class=title>
    <td colspan="2">
        <div class="nowrap"><strong>Sample Input Line:</strong></div>
    </td>
</tr>
<tr class=content>
    <td colspan="2">
        <div class="nowrap">198.93.34.21 - - [01/Aug/2011:00:00:00 -0700] "GET /Sony-BRAVIA-KDL-46V5100-46-Inch-1080p/dp/B001T9N0EO/ HTTP/1.1" 200 12806</div>
    </td>
</tr>
<tr class=content>
    <td colspan="2">
        <div class="nowrap">10.48.58.42 - - [01/Aug/2011:00:00:01 -0700] "GET /PlayStation-Dualshock-Wireless-Controller-Black-3/dp/B0015AARJI/ HTTP/1.1" 200 4253</div>
    </td>
</tr>
<tr class=content>
    <td colspan="2">
        <div class="nowrap">198.93.34.21 - - [01/Aug/2011:00:00:06 -0700] "GET /LG-37LV3500-Accessory-Cables-Cleaning/dp/B00510JKAA/ HTTP/1.1" 200 302</div>
    </td>
</tr>

<tr>
    <td>&nbsp;</td>
    <td align=canter>
        <img width=30 height=30 src="/shs/resources/gfx/down.png">
    </td>
</tr>

<tr class=title>
    <td colspan="2">
        <div class="nowrap"><strong>1. Sample Mapper Input Key-Value Pairs:</strong></div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><i>key</i></div>
    </td>
    <td>
        <div class="nowrap"><i>value</i></div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="Line Number in the file">0</a></div>
    </td>
    <td>
        <div class="nowrap">198.93.34.21 - - [01/Aug/2011:00:00:00 -0700] "GET /Sony-BRAVIA-KDL-46V5100-46-Inch-1080p/dp/B001T9N0EO/ HTTP/1.1" 200 12806</div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="Line Number in the file">1</a></div>
    </td>
    <td>
        <div class="nowrap">10.48.58.42 - - [01/Aug/2011:00:00:01 -0700] "GET /PlayStation-Dualshock-Wireless-Controller-Black-3/dp/B0015AARJI/ HTTP/1.1" 200 4253</div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="Line Number in the file">2</a></div>
    </td>
    <td>
        <div class="nowrap">198.93.34.21 - - [01/Aug/2011:00:00:06 -0700] "GET /LG-37LV3500-Accessory-Cables-Cleaning/dp/B00510JKAA/ HTTP/1.1" 200 302</div>
    </td>
</tr>

<tr>
    <td>&nbsp;</td>
    <td align=canter>
        <img  width=30 height=30 src="/shs/resources/gfx/down.png">
    </td>
</tr>

<tr class=title>
    <td colspan="2">
        <div class="nowrap"><strong>2. Sample Mapper Output Key-Value Pairs:</strong> (Mouse over to see the info about the values)</div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><i>key</i></div>
    </td>
    <td>
        <div class="nowrap"><i>value</i></div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">10.48.58.42</a></div>
    </td>
    <td>
        <div class="nowrap"><a class="red" title="Timetamp in millisecond">1312354684000</a><a class="green" title="Delimiter used by the mapper"><strong>_</strong></a><a class="blue" title="Requested page">/PlayStation-Dualshock-Wireless-Controller-Black-3/dp/B0015AARJI/</a></div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">198.93.34.21</a></div>
    </td>
    <td>
        <div class="nowrap"><a class="red" title="Timetamp in millisecond">1312354683000</a><a class="green" title="Delimiter used by the mapper"><strong>_</strong></a><a class="blue" title="Requested page">/Sony-BRAVIA-KDL-46V5100-46-Inch-1080p/dp/B001T9N0EO/</a></div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">198.93.34.21</a></div>
    </td>
    <td>
        <div class="nowrap"><a class="red" title="Timetamp in millisecond">1312354689000</a><a class="green" title="Delimiter used by the mapper"><strong>_</strong></a><a class="blue" title="Requested page">/LG-37LV3500-Accessory-Cables-Cleaning/dp/B00510JKAA/</a></div>
    </td>
</tr>

<tr>
    <td>&nbsp;</td>
    <td align=canter>
        <a title="Hadoop groups the values by key and sort the keys"><img  width=30 height=30 src="/shs/resources/gfx/shuffle.jpg"></a>
    </td>
</tr>

<tr class=title>
    <td colspan="2">
        <div class="nowrap"><strong>3. Sample Reducer Input Key-Value[] Pairs:</strong></div>
    </td>
</tr>

<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">10.48.58.42</a></div>
    </td>
    <td>
        <div class="nowrap"><a class="red" title="Timetamp in millisecond">1312354684000</a><a class="green" title="Delimiter used by the mapper"><strong>_</strong></a><a class="blue" title="Requested page">/PlayStation-Dualshock-Wireless-Controller-Black-3/dp/B0015AARJI/</a></div>
    </td>
</tr>
<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">198.93.34.21</a></div>
    </td>
    <td>
        <div class="nowrap"><a class="red" title="Timetamp in millisecond">1312354683000</a><a class="green" title="Delimiter used by the mapper"><strong>_</strong></a><a class="blue" title="Requested page">/Sony-BRAVIA-KDL-46V5100-46-Inch-1080p/dp/B001T9N0EO/</a></div>
        <div class="nowrap"><a class="red" title="Timetamp in millisecond">1312354689000</a><a class="green" title="Delimiter used by the mapper"><strong>_</strong></a><a class="blue" title="Requested page">/LG-37LV3500-Accessory-Cables-Cleaning/dp/B00510JKAA/</a></div>
    </td>
</tr>

<tr>
    <td>&nbsp;</td>
    <td align=canter>
        <img  width=30 height=30 src="/shs/resources/gfx/down.png">
    </td>
</tr>

<tr class=title>
    <td colspan="2">
        <div class="nowrap"><strong>4. Sample Reducer Output Content</strong></div>
    </td>
</tr>

<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">10.48.58.42</a></div>
    </td>
    <td>
        <div class="nowrap">1312354684000_/PlayStation-Dualshock-Wireless-Controller-Black-3/dp/B0015AARJI/</div>
    </td>
</tr>

<tr class=content>
    <td>
        <div class="nowrap"><a title="IP">198.93.34.21</a></div>
    </td>
    <td>
        <span class="nowrap, blue">1312354683000_/Sony-BRAVIA-KDL-46V5100-46-Inch-1080p/dp/B001T9N0EO/</span>,<span class="nowrap, red">1312354689000_/LG-37LV3500-Accessory-Cables-Cleaning/dp/B00510JKAA/</span>
    </td>
</tr>
<script>

</script>
