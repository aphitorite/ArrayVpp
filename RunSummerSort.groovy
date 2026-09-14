import io.github.arrayv.shuffles.templates.Shuffle

int nsize = 16384
double factor = 0.5
def sort = { run delegate go nsize.numbers, (factor*nsize/1024.0).speed }
sort.delegate = SkaSort
def shuffles = arrayv.arrayManager.shuffles.clone()

runGroup(shuffles.size()) {
    for (sh in shuffles) {
        if (sh == arrayv.sortAnalyzer.getShuffleById('Already')) {
            arrayv.setCategory("Few Uniques")
            arrayv.setUniqueItems(Math.sqrt(nsize) as int)
            arrayv.arrayManager.setShuffleSingle(arrayv.sortAnalyzer.getShuffleById('RandomShuffle'))
            sort()
            arrayv.setUniqueItems(nsize)
        } else {
			arrayv.setCategory(sh.getName())
            arrayv.arrayManager.setShuffleSingle(sh)
            sort()
        }
    }
}