package io.github.arrayv.sorts.root;

final class SingleState {
   private int leftOverLen;
   private int leftOverFrag;

   protected SingleState(int len, int frag) {
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
