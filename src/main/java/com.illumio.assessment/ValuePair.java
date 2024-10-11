package src.main.java.com.illumio.assessment;

import java.util.Objects;

/**
 *  A generic class to represent a pair of related values. Used to represent port and protocol names in the Log Processor.
 *
 *  @param <A> The type of the first value in the pair.
 *  @param <B> The type of the second value in the pair.
 */
public class ValuePair<A, B> {
  public final A firstValue;
  public final B secondValue;

  public ValuePair(A firstValue, B secondValue) {
    this.firstValue = firstValue;
    this.secondValue = secondValue;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    ValuePair<?, ?> that = (ValuePair<?, ?>) obj;
    return Objects.equals(firstValue, that.firstValue) && Objects.equals(secondValue, that.secondValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstValue, secondValue);
  }

  @Override
  public String toString() {
    return "(" + this.firstValue + " , " + this.secondValue + ")";
  }
}
