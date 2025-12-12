package com.mycompany.acessousuario.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitário para hash e verificação de senhas usando BCrypt
 */
public class PasswordHasher {
    
    /**
     * Gera um hash BCrypt para a senha fornecida
     * @param senha Senha em texto plano
     * @return Hash BCrypt da senha
     */
    public static String hash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }
    
    /**
     * Verifica se a senha fornecida corresponde ao hash armazenado
     * @param senha Senha em texto plano
     * @param hash Hash BCrypt armazenado
     * @return true se a senha corresponde ao hash, false caso contrário
     */
    public static boolean verificar(String senha, String hash) {
        try {
            return BCrypt.checkpw(senha, hash);
        } catch (Exception e) {
            return false;
        }
    }
}

