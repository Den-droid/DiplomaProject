import { PermissionLabel, PermissionName } from "../constants/permissions.constant";

export class Permission {
  constructor(public id: number, public name: string) { }
}

export function mapStringToPermissionName(name: string): PermissionName {
  return PermissionName[name as keyof typeof PermissionName];
}

export function mapStringToPermissionLabel(name: string): PermissionLabel {
  return PermissionLabel[name as keyof typeof PermissionLabel];
}
