export interface UserProfile {
    id: string;
    name: string;
    email: string;
    status: string;
    roles: string[];
    createdAt: string;
}

export interface UpdateProfileRequest {
    fullName: string;
}

export interface ChangePasswordRequest {
    currentPassword: string;
    newPassword: string;
    passwordConfirmation: string;
}
