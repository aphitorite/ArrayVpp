package io.github.arrayv.sorts.root;

final class FcrtState {
   private int leftOverLen;
   private int leftOverFrag;

   protected FcrtState(int len, int frag) {
      this.leftOverLen = len;
      this.leftOverFrag = frag;
   }

   protected int getLeftOverLen() {
      return this.leftOverLen;
   }

   protected int getLeftOverFrag() {
      return this.leftOverFrag;
   }
}
