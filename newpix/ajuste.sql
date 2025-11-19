-- 1. Adicionar uma coluna para controlar se o usuário está ativo
ALTER TABLE usuarios ADD COLUMN ativo TINYINT(1) DEFAULT 1;

-- 2. Remover as chaves estrangeiras que causam a exclusão em cascata
ALTER TABLE transacoes DROP FOREIGN KEY transacoes_ibfk_1;
ALTER TABLE transacoes DROP FOREIGN KEY transacoes_ibfk_2;

-- 3. Recriar as chaves estrangeiras SEM o DELETE CASCADE
-- Isso garante que se tentarmos deletar o usuário fisicamente, o banco impedirá se houver transações,
-- forçando o uso do Soft Delete que implementaremos no Java.
ALTER TABLE transacoes ADD CONSTRAINT transacoes_ibfk_1 FOREIGN KEY (id_remetente) REFERENCES usuarios (id);
ALTER TABLE transacoes ADD CONSTRAINT transacoes_ibfk_2 FOREIGN KEY (id_destinatario) REFERENCES usuarios (id);