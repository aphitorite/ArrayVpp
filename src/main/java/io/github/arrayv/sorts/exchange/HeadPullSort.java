package io.github.arrayv.sorts.exchange;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
/*

PORTED TO ARRAYV BY PCBOYGAMES

------------------------------
- SORTING ALGORITHM MADHOUSE -
------------------------------

U8y3zJTNmM2cVMy2zJLMlMyvQcy4zIHNhVnMts2MzJ4gzLbNg82QzZXMq0HMuM2EzYjMolfMuM2b
zYdBzLXMj8yqWcy2zJHNjMypzKIgzLfNm8yWRsy4zIzMs8yuUsy4zIfMscyiT8y4zYrMhM2FzZVN
zLfNncyeIMy3zYPMvcyzTcy3zJvMrUXMt8yAzLEhzLXMhcymzLogzLfMjsyjScy1zYrNk8ypIMy3
zJDMnc2WQcy4zILNhsylTMy2zIrMjs2VzK9SzLfNoMyKzJ1FzLfMk8ylQcy2zInNnc2NzKZEzLfM
ic2azJ5ZzLXMicyNzKogzLfMjMySzY1XzLfMi8y6Qcy1zJTNgsyuUsy2zL/MmE7Mt8yHzY5FzLXM
k0TMtsyLzL3MmCDMtMyOzL7MsVnMtsyRzKPMrE/MuMyEzYTMoVXMt82YzZfMvCDMtcyPzIjMmVTM
tMy/zY5IzLTMg8yQzZTMskHMtcyRzIDMnsycVMy3zZDMjs2ZIMy0zIjMu0XMuM2EzIXMqMy7Vsy1
zYvMoUXMtsyKzZxOzLTNks2bzK4gzLXNoMyvTcy1zYPNhsydWcy4zJDMhs2cIMy0zYTMpcyfT8y2
zYvMoc2HV8y0zJLMvcy5Tsy1zZvNl8yWIMy1zYrMl03MtsyOzLBFzLXMm82azKlOzLjMklTMuMyB
zYZBzLjNhM2ZTMy0zInNlSDMtcyUzZjNmknMtc2LzL7Mp07MuMybzLvMu1PMtsySzJfMn0HMuM2M
zJDMu07MtcyHzIzNnMyxScy0zZ3Ml1TMuMyAzLDMqlnMtcy9zZMgzLjMgMyszJZJzLjMh8yYU8y0
zZLNms2NIMy3zaDMu0LMt8yPzLDMo0XMuMyUzZLMoc2ZWcy1zInMp0/Mts2AzZ3Mnk7MtMyOzZDM
rETMts2QzZogzLTMgcy5Tcy4zYLNlVnMt82BzL/MuyDMts2BzIvNnMylQ8y4zYLMh8yjzJlPzLjM
vsywzLJOzLjMhsyLzLLMoFTMtcy/zYXMr1LMt8yQzKdPzLjMjsy8TMy2zYHMo8ylIcy1zZfMkc2N
IMy2zYPMn8ydVMy3zL/Nlcy7SMy4zL7MiMyxQcy4zYPMn8yuVMy1zJPNiM2UJ8y0zIrMlM2ZzLFT
zLfMjc2CzJ/MqyDMtcyazKPNjUjMt8yMzKnNjU/Mt8yRzZzNh1fMuMyBzZLNhSDMt82YzJPNiM2Z
Scy3zIvNoM2cIMy2zL7MnUXMtsyOzJDMosypTsy3zYvMmMyhRMy0zIXNjc2ORcy4zZ3Mv8yWRMy1
zJXMq8ykIMy2zI7Nnc2VVcy3zL/NksyeUMy1zITMg8ysIMy2zIHMgMydzKRIzLTMvcy6Rcy0zZvN
m8y6Usy4zZjNksyxzZxFzLfMksyrIMy3zIjMvs2NScy4zZvNhMyyzKVOzLjMgcyezKEgzLfMh8yr
zLlUzLjMjc2dzLxIzLbMlM2EzJ9FzLTMisyDzKHNhSDMtcyKzLJGzLXNl8yOzZXNmUnMuMyGzLnM
nVLMt8ybzJrNmsyeU8y3zIrNlVTMuMy9zILNh8yeIMy3zYDMkcyqUMy4zL3Mqc2HTMy4zL7Nis2Z
Qcy1zYvNnMywQ8y2zYHMkM2FRcy2zJLMjSHMt8yTzZfMncywCgogLSBUaGUgTWFkaG91c2UgQ0VP

[06:57, 03-09-2026:
"S̷̔͘͜T̶̯̒̔Á̸ͅY̶̞͌ ̶͕̫̓͐Ä̸̢͈́W̸͇͛Ȁ̵̪Y̶̢̩̑͌ ̷̖͛F̸̳̮̌Ṟ̸̢̇O̸͕͊̄ͅM̷̞͝ ̷̳̓̽M̷̛̭È̷̱!̵̦̺̅ ̷̣̎I̵͓̩͊ ̷̝͖̐Ḁ̸̂͆L̶͕̯̊̎R̷̝̊͠E̷̥̓Ả̶͍̦͝D̷͚̞̉Ỷ̵̪̍ ̷͍̌̒W̷̺̋A̵̮̔͂R̶̘̿Ṅ̷͎E̵̓D̶̘̋̽ ̴̱̎̾Ỵ̶̬̑Ō̸̡̈́U̷̼͗͘ ̵̙̏̈T̴͎̿H̴͔̲̃̐Ȃ̵̞̜̀T̷͙͐̎ ̴̻̈Ę̸̻̈́̅V̵̡͋E̶̊͜N̴̮͒͛ ̵̯͠M̵̝̓͆Y̸̐̆͜ ̴̥̟̈́O̶̡͇͋W̴̹̒̽N̵̖͛͗ ̵̗͊M̶̰̎E̵̛͚̩N̸̒T̸́͆Ä̸͙́L̴͕̉ ̵͚̔͘I̵̧͋̾N̸̛̻̻S̶̗̟̒A̸̻͌̐Ṉ̵̇̌͜I̴̗͝T̸̰̪̀Y̵͓̽ ̸̬̖̀İ̸̘S̴͚͍͒ ̷̻͠B̷̰̣̏E̸̡͙̔͒Ỷ̵̧Ò̶̞͝N̴̬̎͐D̶͚͐ ̴̹́M̸͕͂Ý̷̻̿ ̶̥́̋͜C̸̣̙͂̇O̸̰̲̾N̸̲̠̆̋T̵̯̿ͅŖ̷̐O̸̼̎Ḷ̶̥́!̵͍͗̑ ̶̟̝̓T̷͕̻̿H̸̱̾̈A̸̟̮̓T̵͈͔̓'̴͙̱̊̔S̷̟̫̍͂ ̵̣͍̚Ȟ̷̩͍Ȏ̷͇͜Ẃ̸͒ͅ ̷͈͙̓͘I̷̋͜͠ ̶̝̾E̶̢̩̎̐N̷̡̘͋D̴͍͎̅E̸̖̿͝D̵̫̤̕ ̶͕̎͝U̷̞̿͒P̵̬̄̃ ̶̝̤́̀H̴̺̽E̴̺͛͛Ṟ̸͒͘͜E̷̫̒ ̷͍̈̾I̸̲̥͛̈́Ń̸̡̞ ̷̫̹̇T̸̼̍͝H̶̟̔̈́E̴̡̊̃ͅ ̵̲̊F̵͕͙͗̎Ĭ̸̹̝R̷̛͚̞̚S̷͕̊T̸͇̞̽̂ ̷̪̀̑P̸̩͇̽L̸͙̾͊A̵̰͋͜Ć̶̐ͅE̶̒̍!̷̝̰̓͗

- The Madhouse CEO"
get some help, man
]
*/
final public class HeadPullSort extends Sort {
	public HeadPullSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Head Pull");
		this.setRunAllSortsName("Head Pull Sort");
		this.setRunSortName("Head Pull Sort");
		this.setCategory("Impractical Sorts");
        this.setConstant("2^n");
        this.setAuthors("PCBoy");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(true);
		this.setUnreasonableLimit(32);
		this.setBogoSort(false);
	}
	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		int i = 1;
		int pull = 1;
		i = 1;
		while (i + 1 <= currentLength) {
			Highlights.markArray(1, i - 1);
			Highlights.markArray(2, i);
			Delays.sleep(0.1);
			if (Reads.compareValues(array[i - 1], array[i]) > 0) {
				pull = i;
				while (pull > 0) {
					Writes.swap(array, pull - 1, pull, 0.1, true, false);
					pull--;
				}
				i = 1;
			} else i++;
		}
	}
}