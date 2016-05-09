package util
import cats.Monoid
object Endo {
  implicit def endo[A] = new Monoid[A => A] {
    type endomorphism = A => A
    override def empty: endomorphism = identity

    // Combine is defined so that |+| chains left to right This might be against convention,
    // but I see it as far more readable. Read f |+| g <==> (g . f)
    override def combine(x: endomorphism, y: endomorphism): endomorphism = a => x(y(a))
  }
}
