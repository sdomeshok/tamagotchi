package models
sealed trait Species
case object Basic extends Species
case object Dragon extends Species
case object Fairy extends Species
case object Ghoul extends Species
case object Dave extends Species

object Species {
  def speciesHungerGrowth(species: Species): Int = species match {
    case Basic => 1
    case Dragon => 4
    case Fairy => 0
    case Ghoul => 2
    case Dave => 1
  }

  def poopProductionRate(species: Species): Int = species match {
    case Basic => 1
    case Dragon => 3
    case Fairy => 2
    case Ghoul => 1
    case Dave => 3
  }

  def boredomProductionRate(species: Species): Int = species match {
    case Basic => 1
    case Dragon => 0
    case Fairy => 4
    case Ghoul => 2
    case Dave => 3
  }
}