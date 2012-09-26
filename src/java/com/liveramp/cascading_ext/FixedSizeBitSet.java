package com.liveramp.cascading_ext;

import java.util.Arrays;

public class FixedSizeBitSet {
  private static final long MAX_VECTOR_SIZE = Integer.MAX_VALUE * 8L;

  private static final byte[] bitvalues = new byte[] {
          (byte) 0x01,
          (byte) 0x02,
          (byte) 0x04,
          (byte) 0x08,
          (byte) 0x10,
          (byte) 0x20,
          (byte) 0x40,
          (byte) 0x80
  };

  public static int getNumBytesToStore(long numBits) {
    return (int) ((numBits + 7) / 8);
  }

  private final byte[] _bytes;
  private final long _numBits;

  /**
   * Create a new FixedSizeBitSet with numBits bit positions. It will be initialized
   * with all positions unset.
   *
   * @param numBits
   */
  public FixedSizeBitSet(long numBits) {
    this(numBits, new byte[getNumBytesToStore(numBits)]);
  }

  /**
   * Create a new FixedSizeBitSet with numBits bit positions using the provided
   * backing array.
   *
   * @param numBits
   * @param arr
   */
  public FixedSizeBitSet(long numBits, byte[] arr) {
    if (numBits > MAX_VECTOR_SIZE) {
      throw new IllegalArgumentException("FixedSizeBitSet only supports up to "
              + MAX_VECTOR_SIZE
              + " bits.");
    }

    if (arr.length < getNumBytesToStore(numBits)) {
      throw new IllegalArgumentException("Provided backing array of length "
              + arr.length
              + " is too small to support a bitvector of " + numBits + " bits.");
    }

    _numBits = numBits;
    _bytes = arr;
  }

  public long numBits() {
    return _numBits;
  }

  public byte[] getRaw() {
    return _bytes;
  }

  public void clear() {
    Arrays.fill(_bytes, (byte) 0);
  }

  public void fill() {
    Arrays.fill(_bytes, (byte) 0xff);
  }

  public boolean get(long pos) {
    return (_bytes[byteNum(pos)] & bitValue(pos)) != 0;
  }

  public void set(long pos) {
    int byteNum = byteNum(pos);
    _bytes[byteNum] = (byte) (_bytes[byteNum] | bitValue(pos));
  }

  public void unset(long pos) {
    int byteNum = byteNum(pos);
    _bytes[byteNum] = (byte) (_bytes[byteNum] ^ bitValue(pos));
  }

  public void flip() {
    for (int i = 0; i < _bytes.length; i++ ) {
      _bytes[i] = (byte) ~_bytes[i];
    }
  }

  public void or(FixedSizeBitSet other) {
    if (other.numBits() != numBits())
      throw new IllegalArgumentException("Must be same size sets");
    byte[] otherBytes = other._bytes;
    for (int i = 0; i < _bytes.length; i++ ) {
      _bytes[i] = (byte) (_bytes[i] | otherBytes[i]);
    }
  }

  public void and(FixedSizeBitSet other) {
    if (other.numBits() != numBits())
      throw new IllegalArgumentException("Must be same size sets");
    byte[] otherBytes = other._bytes;
    for (int i = 0; i < _bytes.length; i++ ) {
      _bytes[i] = (byte) (_bytes[i] & otherBytes[i]);
    }
  }

  public void xor(FixedSizeBitSet other) {
    if (other.numBits() != numBits())
      throw new IllegalArgumentException("Must be same size sets");
    byte[] otherBytes = other._bytes;
    for (int i = 0; i < _bytes.length; i++ ) {
      _bytes[i] = (byte) (_bytes[i] ^ otherBytes[i]);
    }
  }

  private static int byteNum(long bitPos) {
    return (int) (bitPos / 8);
  }

  private static int bitValue(long bitPos) {
    return bitvalues[(int) (bitPos % 8)];
  }
}