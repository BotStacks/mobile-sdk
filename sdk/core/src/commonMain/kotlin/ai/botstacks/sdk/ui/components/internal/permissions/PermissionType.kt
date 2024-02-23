package ai.botstacks.sdk.ui.components.internal.permissions

sealed interface PermissionType {
    data object Camera : PermissionType
    data object Location: PermissionType
}