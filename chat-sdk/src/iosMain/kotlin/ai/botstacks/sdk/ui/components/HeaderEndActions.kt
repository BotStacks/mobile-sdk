package ai.botstacks.sdk.ui.components

sealed interface HeaderEndAction {
    data class Create(val onClick: () -> Unit): HeaderEndAction
    data class Next(val onClick: () -> Unit): HeaderEndAction
    data class Save(val onClick: () -> Unit): HeaderEndAction
    data class Menu(val onClick: () -> Unit): HeaderEndAction
}